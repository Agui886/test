package com.f.stock.controller.index;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import annotation.NotNeedAuth;
import entity.CashinRecord;
import entity.CashoutRecord;
import entity.UserInfo;
import entity.UserRebateDetail;
import entity.common.Response;
import enums.CashinTypeEnum;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mapper.UserFollowUpPositionMapper;
import mapper.UserRebateDetailMapper;
import service.CashinRecordService;
import service.CashoutRecordService;
import service.SysParamConfigService;
import service.UserFollowUpRecordService;
import service.UserInfoService;
import service.UserRebateDetailService;
import utils.SinaApi;
import vo.common.StockQuotesVO;
import vo.manager.CashInOutStatisticsVO;
import vo.manager.DAndWStatisticsVO;
import vo.manager.FollowStatisticsVO;
import vo.manager.FundAnalysisStatisticsPositionResultVO;
import vo.manager.FundAnalysisStatisticsVO;
import vo.manager.IndexUserStatisticsVO;
import vo.manager.RebateStatisticsVO;

@RestController
@RequestMapping("/index")
@Api(tags = "首页报表")
public class IndexController {
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserFollowUpPositionMapper userFollowUpPositionMapper;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserRebateDetailService userRebateDetailService;
	
	@Resource
	private CashinRecordService cashinRecordService;
	
	@Resource
	private CashoutRecordService cashoutRecordService;
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	
	@ApiOperation("用户统计")
	@PostMapping("/config")
	@ResponseBody
	@NotNeedAuth
	public Response<IndexUserStatisticsVO> userStatistics() {
		IndexUserStatisticsVO v = new IndexUserStatisticsVO();
		v.setTotalUsers(userInfoService.count());
		v.setTotalRealUsers(userInfoService.lambdaQuery().eq(UserInfo::getAccountType, 0).count());
		v.setTotalVirtualUsers(userInfoService.lambdaQuery().eq(UserInfo::getAccountType, 1).count());
		LocalDate currentDate = LocalDate.now();
		v.setTodayNewRealUsers(userInfoService.lambdaQuery().ge(UserInfo::getRegTime, currentDate).eq(UserInfo::getAccountType, 0).count());
		v.setTodayNewVirtualUsers(userInfoService.lambdaQuery().ge(UserInfo::getRegTime, currentDate).eq(UserInfo::getAccountType, 1).count());
		v.setTotalOfL1(userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 1).count());
		v.setTotalOfL2(userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 2).count());
		v.setTotalOfL3(userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 3).count());
		v.setTotalOfL4(userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 4).count());
		v.setTotalOfL5(userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 5).count());
		return Response.successData(v);
	}
	
	@ApiOperation("资金分布（资金分析统计）")
    @PostMapping({"/fundAnalysisStatistics"})
    @ResponseBody
    @NotNeedAuth
	public Response<FundAnalysisStatisticsVO> fundAnalysisStatistics() {
		FundAnalysisStatisticsVO vo = new FundAnalysisStatisticsVO();
		//1、查询用户资金统计
		QueryWrapper<UserInfo> uqw = new QueryWrapper<>();
		uqw.select("sum(available_amt) as availableAmtTotal, sum(trading_frozen_amt) as tradingFrozenAmtTotal, sum(follow_amt) as followAmtTotal");
		Map<String, Object> map = userInfoService.getMap(uqw);
		vo.setAvailableAmtTotal((BigDecimal)map.get("availableAmtTotal"));
		vo.setTradingFrozenAmtTotal((BigDecimal)map.get("tradingFrozenAmtTotal"));
		vo.setFollowAmtTotal((BigDecimal)map.get("followAmtTotal"));
		
		//查询持仓市值统计list
		List<FundAnalysisStatisticsPositionResultVO> list = userFollowUpPositionMapper.fundAnalysisStatisticsPositionResultVOList();
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
        //设置-持仓总市值（人名币）-总额
		vo.setMarketValueCnyTotal(marketValueCnyTotal);
		return Response.successData(vo);
	}
	
	@ApiOperation("返佣报表")
	@PostMapping("/rebateStatistics")
	@ResponseBody
	@NotNeedAuth
	public Response<RebateStatisticsVO> rebateStatistics() {
		RebateStatisticsVO v = new RebateStatisticsVO();
		//设置-返佣统计
		v.setRebateStatistics(userRebateDetailService.getRebateStatistics(null,null,null,null));
		//返佣用户list，返佣金额总计排序
		v.setRebateStatisticsList(userRebateDetailService.getUserRebateStatistics(null,null,null,null));
		return Response.successData(v);
	}
	  
	@ApiOperation("充提/提现统计")
	@PostMapping({"/cashInOutStatistics"})
    @ResponseBody
    @NotNeedAuth
	public Response<CashInOutStatisticsVO> cashInOutStatistics(
			@ApiParam("开始时间") @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
			@ApiParam("结束时间") @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
		CashInOutStatisticsVO vo = new CashInOutStatisticsVO();
		vo.setApplyCashInStatistics(cashinRecordService.getCashInOutStatistics(startTime,endTime,0));
		vo.setPassCashInStatistics(cashinRecordService.getCashInOutStatistics(startTime,endTime,1));
		vo.setApplyCashOutStatistics(cashoutRecordService.getCashInOutStatistics(startTime,endTime,0));
		vo.setPassCashOutStatistics(cashoutRecordService.getCashInOutStatistics(startTime,endTime,1));
		return Response.successData(vo);
	}
	
	@ApiOperation("跟投报表")
	@PostMapping({"/followStatistics"})
    @ResponseBody
    @NotNeedAuth
	public Response<FollowStatisticsVO> followStatistics() {
		FollowStatisticsVO vo = new FollowStatisticsVO();
		vo.setFollowStatistics(userFollowUpRecordService.getFollowStatistics(null));
		vo.setFollowStatistics0(userFollowUpRecordService.getFollowStatistics(0));
		vo.setFollowStatistics1(userFollowUpRecordService.getFollowStatistics(1));
		vo.setFollowStatistics2(userFollowUpRecordService.getFollowStatistics(2));
		vo.setFollowStatistics3(userFollowUpRecordService.getFollowStatistics(3));
		vo.setFollowStatistics4(userFollowUpRecordService.getFollowStatistics(4));
		vo.setFollowStatistics5(userFollowUpRecordService.getFollowStatistics(5));
		vo.setFollowStatistics6(userFollowUpRecordService.getFollowStatistics(6));
		return Response.successData(vo);
	}
	
}
