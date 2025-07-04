package com.f.stock.controller.follow;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import annotation.NotNeedAuth;
import entity.UserFollowUpPending;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserFollowUpPendingService;
import vo.manager.FollowPendingListSearchParamVO;
import vo.manager.FollowPendingTransferDetailUserVO;
import vo.manager.FollowPendingTransferDetailVO;
import vo.manager.FollowPendingTransferFinishParamVO;
import vo.manager.FollowTransferOperationParamVO;

@RestController
@RequestMapping("/follow/pending")
@Api(tags = "跟投管理")
public class FollowPendingController extends BaseController {
	
	@Resource
	private UserFollowUpPendingService userFollowUpPendingService;
	
	@ApiOperation("用户跟投委托")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserFollowUpPending>> list(@RequestBody FollowPendingListSearchParamVO param) {
		Page<UserFollowUpPending> page = new Page<>(param.getPageNo(), param.getPageSize());
		userFollowUpPendingService.managerList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("委托订单-待转完成-详情")
	@PostMapping("/transferDetail")
	@ResponseBody
	public Response<FollowPendingTransferDetailVO> transferDetail(
			@ApiParam("委托单ID,List") @RequestParam("pendingIdList") List<Integer> pendingIdList,
			@ApiParam("信号id") @RequestParam("signId") Integer signId,
			@ApiParam("购买价格") @RequestParam(value = "buyingPrice",defaultValue = "", required = false) BigDecimal buyingPrice,
			@ApiParam("是否市场交易价") @RequestParam(value = "marketPrice",defaultValue = "", required = false) Boolean marketPrice) {
		return userFollowUpPendingService.transferDetail(pendingIdList,signId,buyingPrice,marketPrice);
	}
	
	@ApiOperation("委托订单-完成操作")
	@PostMapping("/transferFinish")
	@ResponseBody
	public Response<Void> transferFinish(@RequestBody FollowPendingTransferFinishParamVO param) {
		return userFollowUpPendingService.transferFinish(param,super.getIp(),super.getUser().getOperator());
	}
	
	@ApiOperation("委托订单-撤单操作")
	@PostMapping("/revoke")
	@ResponseBody
	public Response<Void> revoke(@ApiParam("委托单ID,List") @RequestParam("pendingIdList") List<Integer> pendingIdList) {
		return userFollowUpPendingService.revoke(pendingIdList,super.getIp(),super.getUser().getOperator());
	}
	
}
