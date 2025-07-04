package com.f.stock.controller.follow;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import cn.hutool.core.date.DateUtil;
import entity.SignInfo;
import entity.UserFollowUpPending;
import entity.UserFollowUpRecord;
import entity.common.Response;
import enums.UserFollowStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SignInfoService;
import service.StockInfoService;
import service.UserFollowUpPendingService;
import service.UserFollowUpRecordService;
import vo.common.StockDayTransactionParamVO;
import vo.common.StockDayTransactionVO;
import vo.manager.SignListSearchParamVO;
import vo.server.GetReleaseSaleSignVO;

@RestController
@RequestMapping("/follow/sign")
@Api(tags = "跟投管理")
public class FollowSignInfoController extends BaseController {
	
	@Resource
	private SignInfoService signInfoService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private UserFollowUpPendingService userFollowUpPendingService;
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	@ApiOperation("跟投信号")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SignInfo>> list(@RequestBody SignListSearchParamVO param) {
		Page<SignInfo> page = new Page<>(param.getPageNo(), param.getPageSize());
		signInfoService.managerList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("跟投信号-发布信号")
	@PostMapping("/release")
	@ResponseBody
	public Response<Void> release(@RequestBody SignInfo signInfo) {
		return signInfoService.release(signInfo, super.getUser().getOperator());
	}
	
	@ApiOperation("跟投信号-配置信号")
	@PostMapping("/configure")
	@ResponseBody
	public Response<Void> configure(@RequestBody SignInfo signInfo) {
		return signInfoService.configure(signInfo);
	}
	
	@ApiOperation("跟投信号-删除")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("信号id") @RequestParam("id") Integer id) {
		//此删除只删除了主数据，关联数据未处理，慎用
		signInfoService.removeById(id);
		return Response.success();
	}
	
	@ApiOperation("跟投信号-跟投信号详情")
	@PostMapping("/getSignInfo")
	@ResponseBody
	public Response<SignInfo> getSignInfo(@RequestParam("signId") Integer signId) {
		SignInfo signInfo = signInfoService.getById(signId);
		return Response.successData(signInfo);
	}
	
	@ApiOperation("跟投信号-跟投信号详情-获取股票(分时成交记录)")
	@PostMapping("/getStockDayTransaction")
	@ResponseBody
	public Response<StockDayTransactionVO> getStockDayTransaction(@RequestBody StockDayTransactionParamVO param){
		
		if (param.getStockCode().isEmpty() || param.getStockType().isEmpty()) {
			return Response.fail("stockCode,stockType必填");
		}
		
		 if (param.getDisplayReleaseTime() != null) {
			// 判断displayReleaseTime是否大于当前时间
	        if (param.getDisplayReleaseTime().after(new Date())) {
	        	return Response.fail("该信号显示时间为"+DateUtil.formatDateTime(param.getDisplayReleaseTime()) +"，无法查询该股票"+param.getStockCode()+"的价格");
	        }
		 }
		 StockDayTransactionVO stockDayTransactionVO = stockInfoService.getStockDayTransaction(param);
		
		if (stockDayTransactionVO == null) {
			return Response.fail("股票代码或股票类型出错，无数据");
		}
		return Response.successData(stockDayTransactionVO);
	}
	
	@ApiOperation("跟投信号-获取（发布卖出信号详情数据）")
	@PostMapping("/getReleaseSaleSign")
	@ResponseBody
	public Response<GetReleaseSaleSignVO> getReleaseSaleSignVO(
			@ApiParam("买入信号id") @RequestParam("buySignId") Integer buySignId,
			@ApiParam("导师id") @RequestParam("tutorId") Integer tutorId,
			@ApiParam("项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投") @RequestParam("itemType") Integer itemType){
		
		if (buySignId == null || tutorId == null || itemType == null) {
			return Response.fail("buySignId、tutorId、itemType必填");
		}
		GetReleaseSaleSignVO getReleaseSaleSignVO = new GetReleaseSaleSignVO();
		//2.1、获取，当前导师，当前项目的，跟投记录，不包含已结束的记录
		List<UserFollowUpRecord> userFollowUpRecordList = userFollowUpRecordService.lambdaQuery()
				.eq(UserFollowUpRecord::getTutorId, tutorId)//导师
				.eq(UserFollowUpRecord::getItemType, itemType)//项目
				.ne(UserFollowUpRecord::getFollowStatus, UserFollowStatusEnum.FINISH.getCode())//不包含（已结束）
				.list();
		getReleaseSaleSignVO.setUserFollowUpRecordList(userFollowUpRecordList);
		//3.1、获取，买入委托单
		List<UserFollowUpRecord> yestUserFollowUpRecord = new ArrayList<UserFollowUpRecord>();
		List<UserFollowUpPending> userFollowUpPendingList = userFollowUpPendingService.lambdaQuery()
				.eq(UserFollowUpPending :: getSignId, buySignId)//买入信号id
				.list();
		for(UserFollowUpPending userFollowUpPending : userFollowUpPendingList) {
			UserFollowUpRecord userFollowUpRecord = userFollowUpRecordService.getById(userFollowUpPending.getFollowRecordId());
			if (userFollowUpRecord != null) {
				yestUserFollowUpRecord.add(userFollowUpRecord);
			}
		}
		getReleaseSaleSignVO.setYestUserFollowUpRecord(yestUserFollowUpRecord);
		
		return Response.successData(getReleaseSaleSignVO);
	}
}
