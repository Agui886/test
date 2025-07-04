package service.impl;

import entity.UserFavoriteStock;
import entity.common.Response;
import entity.common.StockInfo;
import enums.StockTypeEnum;
import mapper.UserFavoriteStockMapper;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserFavoriteStockService;
import utils.SinaApi;
import vo.common.StockQuotesVO;
import vo.server.AddFavoriteStockParamVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户自选股票信息 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
@Service
public class UserFavoriteStockServiceImpl extends ServiceImpl<UserFavoriteStockMapper, UserFavoriteStock> implements UserFavoriteStockService {

	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Override
	public void favoriteList(Page<UserFavoriteStock> page, Integer userId) {
		this.lambdaQuery().eq(UserFavoriteStock::getUserId, userId).orderByDesc(UserFavoriteStock::getAddTime).page(page);
		List<UserFavoriteStock> list = page.getRecords();
		List<String> stockGids = new ArrayList<>();
		List<String> hkOrUsStockGids = new ArrayList<>();
		list.forEach(i->{
			if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
					|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
				stockGids.add(i.getStockType() + i.getStockCode());
			} else {
				hkOrUsStockGids.add(i.getStockType() + i.getStockCode());
			}
		});
		if(stockGids.size() > 0) {
			 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
			 list.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					 }
				 });
			 });
		}
		if(hkOrUsStockGids.size() > 0) {
			 List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
			 list.forEach(i-> {
				 stockQuotesVOList.forEach(stockQuotesVO-> {
					 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
						 i.setPercentageIncrease(stockQuotesVO.getPercentageIncrease());
					     i.setNowPrice(stockQuotesVO.getNowPrice());
					     i.setPrevClose(stockQuotesVO.getPrevClose());
					 }
				 });
			 });
		}
	}

	
	@Override
	@Transactional
	public Response<Void> addStocks(Integer userId, List<AddFavoriteStockParamVO> param) {
		if(param.size() == 0) {
			return Response.fail("请选择您要添加的股票");
		}
		List<UserFavoriteStock> addList = new ArrayList<>();
		for(AddFavoriteStockParamVO p : param) {
			if(this.lambdaQuery()
					.eq(UserFavoriteStock::getUserId, userId)
					.eq(UserFavoriteStock::getStockCode, p.getStockCode())
					.eq(UserFavoriteStock::getStockType, p.getStockType()).count() > 0) {
				continue;
			}
			UserFavoriteStock ufs = new UserFavoriteStock();
			ufs.setUserId(userId);
			ufs.setStockCode(p.getStockCode());
			ufs.setStockName(p.getStockName());
			ufs.setStockType(p.getStockType());
			ufs.setStockPlate(p.getStockPlate());
			addList.add(ufs);
		}
		this.saveBatch(addList);
		return Response.success();
	}
}
