package service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.CashoutRecord;
import entity.TutorInfo;
import entity.UserFollowUpPosition;
import entity.UserFollowUpRecord;
import entity.UserInfo;
import entity.UserRebateDetail;
import entity.common.StockInfo;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import enums.UserFollowStatusEnum;
import mapper.UserFollowUpPositionMapper;
import mapper.UserFollowUpRecordMapper;
import service.StockInfoService;
import service.SysParamConfigService;
import service.TutorInfoService;
import service.UserFollowUpPositionService;
import service.UserFollowUpRecordService;
import service.UserInfoService;
import utils.NowStockApi;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockQuotesVO;
import vo.manager.FollowRecordParamVO;
import vo.manager.FundAnalysisStatisticsPositionResultVO;
import vo.manager.CashInOutStatisticsVO.CashInOutStatistics;
import vo.manager.FollowStatisticsVO.FollowStatistics;
import vo.manager.RebateStatisticsVO.RebateStatistics;
import vo.server.UserFollowRecordVO;

/**
 * <p>
 * 用户跟投记录表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
@Service
public class UserFollowUpRecordServiceImpl extends ServiceImpl<UserFollowUpRecordMapper, UserFollowUpRecord> implements UserFollowUpRecordService {

	@Resource
	private UserFollowUpPositionService userFollowUpPositionService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	
	/**
	 * 跟投-导师信息-用户跟投记录
	 */
	@Override
	public List<UserFollowRecordVO> userFollowRecord(UserFollowUpRecord param) {
		List<UserFollowRecordVO> list = new ArrayList<UserFollowRecordVO>();
		//查询用户跟投记录，不包含已结束的记录
		List<UserFollowUpRecord> userFollowUpRecordList = this.lambdaQuery()
				.eq(UserFollowUpRecord :: getUserId, param.getUserId())
				.ne(UserFollowUpRecord :: getFollowStatus, UserFollowStatusEnum.FINISH.getCode())
				.eq(param.getTutorId() != null, UserFollowUpRecord :: getTutorId, param.getTutorId())
				.eq(param.getItemType() != null, UserFollowUpRecord :: getItemType, param.getItemType())
				.eq(param.getId() != null, UserFollowUpRecord :: getId, param.getId())
				.list();
		for (UserFollowUpRecord userFollowUpRecord : userFollowUpRecordList) {
			UserFollowRecordVO userFollowRecordVO = new UserFollowRecordVO();
			//userFollowUpRecord对象相同属性，赋值进UserFollowRecordVO中
			BeanUtils.copyProperties(userFollowUpRecord, userFollowRecordVO);
			//根据跟投id，计算总市值
			userFollowRecordVO.setPositionFollowSum(getPositionFollowSum(userFollowUpRecord.getId()));
			//导师名称
			TutorInfo tutorInfo = tutorInfoService.getById(userFollowRecordVO.getTutorId());
			if (tutorInfo != null && StringUtil.isNotEmpty(tutorInfo.getTutorName())) {
				userFollowRecordVO.setTutorName(tutorInfo.getTutorName());
			}
			list.add(userFollowRecordVO);
		}
		return list;
	}

	/**
	 * 跟投管理-用户跟投记录
	 */
	@Override
	public void managerList(Page<UserFollowUpRecord> page, FollowRecordParamVO param) {
		this.baseMapper.managerList(page,param);
	}
	
	/**
	 * 根据跟投id，计算总市值
	 */
	@Override
	public BigDecimal getPositionFollowSum(Integer followRecordId) {
		//查询统计数据
		QueryWrapper<UserFollowUpPosition> cqw = new QueryWrapper<>();
		cqw.eq("follow_record_id", followRecordId);//跟投记录id
		cqw.eq("position_status", 0);//持仓状态，0-正式持仓
		cqw.gt("hold_shares", 0);//当前持有-股数
		cqw.select("stock_type, stock_code, COALESCE(SUM(hold_shares),0) AS total_shares");
		cqw.groupBy("stock_type, stock_code");         // GROUP BY user_id分组
		List<Map<String, Object>> mapList = userFollowUpPositionService.listMaps(cqw);
		//存储list对象
		List<FundAnalysisStatisticsPositionResultVO> list = mapList.stream().map(entity -> {
			FundAnalysisStatisticsPositionResultVO fundAnalysisStatisticsPositionResultVO = new FundAnalysisStatisticsPositionResultVO();
			//存储入对象中
			fundAnalysisStatisticsPositionResultVO.setStockType((String)entity.get("stock_type"));
			fundAnalysisStatisticsPositionResultVO.setStockCode((String)entity.get("stock_code"));
			fundAnalysisStatisticsPositionResultVO.setTotalShares(((BigDecimal)entity.get("total_shares")).intValue());
			return fundAnalysisStatisticsPositionResultVO;
		}).collect(Collectors.toList());
		//以下代码复用率高，最好封装
		if(list.size() >= 0) {
			BigDecimal usdRate = null, hkdRate = null;
			List<String> stockGids = new ArrayList<>();
			List<String> hkOrUsStockGids = new ArrayList<>();
			for(FundAnalysisStatisticsPositionResultVO i : list) {
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
						     BigDecimal totalShares = new BigDecimal(i.getTotalShares());
						     i.setMarketValue(i.getNowPrice().multiply(totalShares));
						 }
					 });
				 });
			}
			//处理港股或美股的数据
			if (hkOrUsStockGids.size() > 0) {
				List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
				for (FundAnalysisStatisticsPositionResultVO i : list) {
					for (StockQuotesVO stockQuotesVO : stockQuotesVOList) {
						if (stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							i.setNowPrice(stockQuotesVO.getNowPrice());
							BigDecimal totalShares = new BigDecimal(i.getTotalShares());
						    i.setMarketValue(i.getNowPrice().multiply(totalShares));
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
		//统计-持仓市值统计list，人名币总市值
		BigDecimal marketValueCnyTotal = BigDecimal.ZERO;
        for (FundAnalysisStatisticsPositionResultVO fundAnalysisStatisticsPositionResultVO : list) {
            BigDecimal marketValueCny = fundAnalysisStatisticsPositionResultVO.getMarketValueCny();
            if (marketValueCny != null) {
            	marketValueCnyTotal = marketValueCnyTotal.add(marketValueCny);
            }// 如果为null则跳过（表示该条目值无效）
        }
		
		return marketValueCnyTotal;
	}

	/**
	 * 跟投报表-跟投统计
	 */
	@Override
	public FollowStatistics getFollowStatistics(Integer itemType) {
		FollowStatistics followStatistics = new FollowStatistics();
		//查询统计数据
		QueryWrapper<UserFollowUpRecord> cqw = new QueryWrapper<>();
		if (itemType != null) {
			cqw.eq("item_type", itemType);//跟投类型
		}
		cqw.select("COUNT(DISTINCT user_id) AS distinct_user_count, " //user_id去重的总数
			     + "COUNT(DISTINCT tutor_id) AS distinct_tutor_count, " //tutor_id去重的总数
			     + "COUNT(*) AS total_records, "  //总记录数
			     + "COALESCE(SUM(initial_follow_sum), 0) AS total_initial_follow_sum, " //initial_follow_sum总额
			     + "COALESCE(SUM(return_follow_sum), 0) AS total_return_follow_sum, " //return_follow_sum总额
			     + "COALESCE(SUM(tutor_commission), 0) AS total_tutor_commission, " //tutor_commission总额
			     + "COALESCE(SUM(platform_commission), 0) AS total_platform_commission " //platform_commission总额
				);
		Map<String, Object> map = this.getMap(cqw);
		//存储入对象中
		followStatistics.setDistinctUserCount(((Long)map.get("distinct_user_count")).intValue());
		followStatistics.setDistinctTutorCount(((Long)map.get("distinct_tutor_count")).intValue());
		followStatistics.setTotalRecords(((Long)map.get("total_records")).intValue());
		followStatistics.setTotalInitialFollowSum((BigDecimal)map.get("total_initial_follow_sum"));
		followStatistics.setTotalReturnFollowSum((BigDecimal)map.get("total_return_follow_sum"));
		followStatistics.setTotalTutorCommission((BigDecimal)map.get("total_tutor_commission"));
		followStatistics.setTotalPlatformCommission((BigDecimal)map.get("total_platform_commission"));
		return followStatistics;
	}



}
