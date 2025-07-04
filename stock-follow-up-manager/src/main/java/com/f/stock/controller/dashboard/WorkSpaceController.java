package com.f.stock.controller.dashboard;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import constant.Constant;
import entity.CashinRecord;
import entity.CashoutRecord;
import entity.UserInfo;
import entity.UserStockPosition;
import entity.common.Response;
import enums.StockPendingStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mapper.UserStockPositionMapper;
import service.CashinRecordService;
import service.CashoutRecordService;
import service.StockInfoService;
import service.UserInfoService;
import service.UserStockClosingPositionService;
import service.UserStockPositionService;
import utils.SinaApi;
import vo.manager.DAndWStatisticsVO;
import vo.manager.MarketVO;
import vo.manager.ProportionStatisticsVO;
import vo.manager.StockPositionListVO;
import vo.manager.UserCountVO;

@RestController
@RequestMapping("/dashboard/workspace")
@Api(tags = "仪表盘-工作台")
public class WorkSpaceController {
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;
	
	@Resource
	private UserStockPositionMapper userStockPositionMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private CashinRecordService cashinRecordService;
	
	@Resource
	private CashoutRecordService cashoutRecordService;
	
	@ApiOperation("大盘指数")
    @PostMapping({"/getMarket"})
    @ResponseBody
    public Response<List<MarketVO>> getMarket() {
		return Response.successData(SinaApi.getMarket());
    }
	
	@ApiOperation("用户数量统计")
    @PostMapping({"/userCount"})
    @ResponseBody
    public Response<UserCountVO> userCount() {
		UserCountVO vo = new UserCountVO();
		//vo.setAgentCount(agentInfoService.lambdaQuery().count());
		vo.setRealUserCount(userInfoService.lambdaQuery().eq(UserInfo::getAccountType, Constant.ACCOUNT_TYPE_REAL).count());
		vo.setVirtualUserCount(userInfoService.lambdaQuery().eq(UserInfo::getAccountType, Constant.ACCOUNT_TYPE_VISUAL).count());
		return Response.successData(vo);
    }
	
	@ApiOperation("占比统计")
    @PostMapping({"/proportionStatistics"})
    @ResponseBody
	public Response<ProportionStatisticsVO> proportionStatistics() {
		ProportionStatisticsVO vo = new ProportionStatisticsVO();
		//vo.setStockCount(stockInfoService.count());
		QueryWrapper<UserInfo> uqw = new QueryWrapper<>();
		uqw.select("sum(available_amt) as availableAmt, sum(available_amt + trading_frozen_amt + ipo_amt) as totalAmt");
		Map<String, Object> map = userInfoService.getMap(uqw);
		vo.setUserEnableAmount((BigDecimal)map.get("availableAmt"));
		vo.setUserTotalAmount((BigDecimal)map.get("totalAmt"));
		vo.setClosingPositionCount(userStockClosingPositionService.lambdaQuery().count());
		vo.setPositionCount(userStockPositionService.lambdaQuery().eq(UserStockPosition::getPositionStatus, StockPendingStatusEnum.COMPLETED.getCode()).count());
		QueryWrapper<CashinRecord> cqw = new QueryWrapper<>();
		cqw.eq("order_status", 1);
		cqw.select("ifnull(sum(final_amount),0) as final_amount");
		map = cashinRecordService.getMap(cqw);
		vo.setTotalCashinAmount((BigDecimal)map.get("final_amount"));
		return Response.successData(vo);
	}
	
	@ApiOperation("持仓动态")
	@PostMapping({"/newStockPositions"})
    @ResponseBody
	public Response<List<StockPositionListVO>> newStockPositions() {
		List<StockPositionListVO> list = userStockPositionMapper.newStockPositions();
		return Response.successData(list);
	}
	
	@ApiOperation("充提统计")
	@PostMapping({"/dAndWStatistics"})
    @ResponseBody
	public Response<DAndWStatisticsVO> dAndWStatistics() {
		Calendar calendar = Calendar.getInstance();
     	calendar.add(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) * -1);
 		calendar.add(Calendar.MINUTE, calendar.get(Calendar.MINUTE) * -1);
 		calendar.add(Calendar.SECOND, calendar.get(Calendar.SECOND) * -1);
 		calendar.add(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND) * -1);				
 		Date today = calendar.getTime();
		DAndWStatisticsVO vo = new DAndWStatisticsVO();
		QueryWrapper<CashinRecord> cqw = new QueryWrapper<>();
		cqw.eq("order_status", 1);
		cqw.select("ifnull(sum(final_amount),0) as final_amount");
		Map<String, Object> map = cashinRecordService.getMap(cqw);
		vo.setTotalCashinAmount((BigDecimal)map.get("final_amount"));
		cqw.ge("request_time", today);
		map = cashinRecordService.getMap(cqw);
		vo.setTodayCashinAmount((BigDecimal)map.get("final_amount"));
		QueryWrapper<CashoutRecord> dqw = new QueryWrapper<>();
		dqw.eq("order_status", 1);
		dqw.select("ifnull(sum(final_amount),0) as finalAmount");
		map = cashoutRecordService.getMap(dqw);
		vo.setTotalCashoutAmount((BigDecimal)map.get("finalAmount"));
		dqw.ge("request_time", today);
		map = cashoutRecordService.getMap(dqw);
		vo.setTodayCashoutAmount((BigDecimal)map.get("finalAmount"));
		return Response.successData(vo);
	}
}
