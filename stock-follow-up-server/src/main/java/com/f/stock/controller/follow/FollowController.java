package com.f.stock.controller.follow;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserFollowUpPosition;
import entity.UserFollowUpRecord;
import entity.UserInfo;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import service.IpAddressService;
import service.UserFollowUpPendingService;
import service.UserFollowUpPositionService;
import service.UserFollowUpRecordService;
import service.UserFollowUpRequestService;
import service.UserInfoService;
import vo.common.TokenUserVO;
import vo.manager.FollowPositionListSearchParamVO;
import vo.server.ApplyFollowRequestVO;
import vo.server.UserFollowPendingParamVO;
import vo.server.UserFollowPendingVO;
import vo.server.UserFollowRecordVO;
import vo.server.UserFollowRequestOrRecordParamVO;
import vo.server.UserFollowRequestOrRecordVO;

@Controller
@RequestMapping("/follow")
@Api(tags = "跟投")
public class FollowController extends BaseController {
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	@Resource
	private UserFollowUpPendingService userFollowUpPendingService;
	
	@Resource
	private UserFollowUpRequestService userFollowUpRequestService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private UserFollowUpPositionService userFollowUpPositionService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@ApiOperation("跟投-用户跟投记录-跟投信息（暂时废弃）")
	@PostMapping("/userFollowRecordInfo")
	@ResponseBody
	public Response<UserFollowRecordVO> userFollowRecordInfo(
			@ApiParam("跟投记录id") @RequestParam(value = "followRecordId") Integer followRecordId
			) {
		if (followRecordId == null) {
			return Response.fail("followRecordId必传");
		}
		
		UserFollowUpRecord param = new UserFollowUpRecord();
		param.setUserId(super.getTokenUser().getLoginId());
		param.setId(followRecordId);
		List<UserFollowRecordVO> list = userFollowUpRecordService.userFollowRecord(param);
		if (list.size() > 0) {
			UserFollowRecordVO userFollowRecordVO = list.get(0);
			return Response.successData(userFollowRecordVO);
		}else {
			return Response.fail("无跟投信息");
		}
	}
	
	@ApiOperation("跟投-导师信息-跟单详情-申请跟投")
	@PostMapping("/applyFollowRequest")
	@ResponseBody
	public Response<Void> applyFollowRequest(@RequestBody ApplyFollowRequestVO param) {
		TokenUserVO tu = super.getTokenUser();
		UserInfo userInfo = userInfoService.getById(tu.getLoginId());
		if (userInfo != null && userInfo.getFollowEnable()) {
			param.setUserId(tu.getLoginId());
			param.setIp(super.getIp());
			param.setIpAddress(ipAddressService.getIpAddress(super.getIp()).getAddress2());
			param.setOperator(tu.getOperator());
			return userFollowUpRequestService.applyFollowRequest(param);
		}else {
			return Response.fail("当前用户不允许跟投");
		}
		
	}
	
	@ApiOperation("跟投-用户跟投记录（跟投审核记录/跟投记录）")
	@PostMapping("/userFollowRequestOrRecord")
	@ResponseBody
	public Response<Page<UserFollowRequestOrRecordVO>> userFollowRequestOrRecord(@RequestBody UserFollowRequestOrRecordParamVO param) {
		Page<UserFollowRequestOrRecordVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		return userFollowUpRequestService.userFollowRequestOrRecord(page,param);
	}
	
	@ApiOperation("跟投-导师-项目-跟投详情（平仓记录）")
	@PostMapping("/UserFollowPosition")
	@ResponseBody
	public Response<Page<UserFollowUpPosition>> UserFollowPosition(@RequestBody FollowPositionListSearchParamVO param) {
		if (param.getPositionStatus() == null || param.getPositionStatus() != 1) {
			return Response.fail("positionStatus必传，并只能为，已平仓");
		}
		if (param.getFollowRecordId() == null) {
			return Response.fail("followRecordId必传");
		}
		if (param.getTutorId() == null) {
			return Response.fail("tutorId必传");
		}
		Page<UserFollowUpPosition> page = new Page<>(param.getPageNo(), param.getPageSize());
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		userFollowUpPositionService.UserFollowPosition(page,param);
		return Response.successData(page);
	}
	
	@ApiOperation("跟投-导师-项目-跟投-信号详情（对应后台跟随记录）")
	@PostMapping("/userFollowPending")
	@ResponseBody
	public Response<Page<UserFollowPendingVO>> userFollowPending(@RequestBody UserFollowPendingParamVO param) {
		if (param.getPositionId() == null || param.getPositionId() <= 0) {
			return Response.fail("positionId必传");
		}
		Page<UserFollowPendingVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		userFollowUpPendingService.userFollowPending(page,param);
		return Response.successData(page);
	}
	
	@ApiOperation("跟投-用户跟投记录(只查跟投中的 )")
	@PostMapping("/userFollowRecord")
	@ResponseBody
	public Response<List<UserFollowRecordVO>> userFollowRecord(
			@ApiParam("导师id") @RequestParam(value = "tutorId", defaultValue = "", required = false) Integer tutorId
			) {
		UserFollowUpRecord param = new UserFollowUpRecord();
		param.setUserId(super.getTokenUser().getLoginId());
		param.setTutorId(tutorId);
		List<UserFollowRecordVO> list = userFollowUpRecordService.userFollowRecord(param);
		return Response.successData(list);
	}

}
