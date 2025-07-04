package service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.UserFollowUpPosition;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import mapper.UserFollowUpPositionMapper;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFollowUpPositionService;
import utils.SinaApi;
import vo.common.StockQuotesVO;
import vo.manager.FollowPositionListSearchParamVO;

/**
 * <p>
 * 用户-跟投-持仓信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
@Service
public class UserFollowUpPositionServiceImpl extends ServiceImpl<UserFollowUpPositionMapper, UserFollowUpPosition> implements UserFollowUpPositionService {

	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;

	
	/**
	 * 跟投管理-用户跟投持仓
	 */
	@Override
	public void managerList(Page<UserFollowUpPosition> page, FollowPositionListSearchParamVO param) {
		this.baseMapper.managerList(page,param);
		List<UserFollowUpPosition> list = page.getRecords();
		//以下代码复用率高，最好封装
		if(list.size() == 0) 
			return;
		BigDecimal usdRate = null, hkdRate = null;
		List<String> stockGids = new ArrayList<>();
		List<String> hkOrUsStockGids = new ArrayList<>();
		for(UserFollowUpPosition i : list) {
			String gid = i.getStockType() + i.getStockCode();
			if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
				if(!stockGids.contains(gid)) {
					stockGids.add(gid);
				}
			} else {
				if(!hkOrUsStockGids.contains(gid)) {
					hkOrUsStockGids.add(gid);
				}
			}
		}
		//处理A股的数据
		if(stockGids.size() > 0) {
			 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
			 list.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     BigDecimal holdShares = new BigDecimal(i.getHoldShares());
					     i.setMarketValue(i.getNowPrice().multiply(holdShares));
					 }
				 });
			 });
		}
		//处理港股或美股的数据
		if (hkOrUsStockGids.size() > 0) {
			List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
			for (UserFollowUpPosition i : list) {
				for (StockQuotesVO stockQuotesVO : stockQuotesVOList) {
					if (stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						i.setNowPrice(stockQuotesVO.getNowPrice());
						BigDecimal holdShares = new BigDecimal(i.getHoldShares());
					    i.setMarketValue(i.getNowPrice().multiply(holdShares));
						if (i.getStockType().equals(StockTypeEnum.US.getCode())) {
							if (usdRate == null) {
								usdRate = sysParamConfigService.getExchangeRate(CurrencyEnum.USD);
							}
							i.setExchangeRate(usdRate);
						} else {
							if (hkdRate == null) {
								hkdRate = sysParamConfigService.getExchangeRate(CurrencyEnum.HKD);
							}
							i.setExchangeRate(hkdRate);
						}
					}
				}
			}
		}
	}



	/**
	 * 获取-初始化，用户持仓数据（获取用户可合并，持仓数据，有就合并，没有就创建）
	 */
	@Override
	public UserFollowUpPosition getInitUserPositionMerge(Integer userId, Integer tutorId, Integer followRecordId,
			String stockName,String stockCode, String stockType) {
		//获取用户可合并，持仓数据，以上条件缺一不可
		UserFollowUpPosition userFollowUpPosition = this.lambdaQuery().
						eq(UserFollowUpPosition::getUserId, userId).
						eq(UserFollowUpPosition::getTutorId, tutorId).
						eq(UserFollowUpPosition::getFollowRecordId, followRecordId).
						eq(UserFollowUpPosition::getPositionStatus, 0).
						eq(UserFollowUpPosition::getStockCode,stockCode).
						eq(UserFollowUpPosition::getStockType, stockType).
						one();
		//如果没有持仓
		if(userFollowUpPosition == null) {
			//设置新增的0持仓数据
			userFollowUpPosition= new UserFollowUpPosition();
			userFollowUpPosition.setUserId(userId);
			userFollowUpPosition.setTutorId(tutorId);
			userFollowUpPosition.setFollowRecordId(followRecordId);
			userFollowUpPosition.setCreatPositionTime(new Date());
			userFollowUpPosition.setPositionStatus(0);//正式持仓
			userFollowUpPosition.setStockName(stockName);
			userFollowUpPosition.setStockCode(stockCode);
			userFollowUpPosition.setStockType(stockType);
			userFollowUpPosition.setBuyingCostPrice(new BigDecimal(0));//成本价,默认0
			userFollowUpPosition.setHoldShares(0);//持有股数，默认0
			//新增持仓数据
			userFollowUpPosition.insert();
		}
		return userFollowUpPosition;
	}



	/**
	 * 跟投-跟投详情（平仓记录）
	 */
	@Override
	public void UserFollowPosition(Page<UserFollowUpPosition> page, FollowPositionListSearchParamVO param) {
		this.baseMapper.managerList(page,param);
	}
	
}
