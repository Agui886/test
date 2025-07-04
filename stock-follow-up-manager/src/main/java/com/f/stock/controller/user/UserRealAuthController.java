package com.f.stock.controller.user;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserRealAuthInfo;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserRealAuthInfoService;
import vo.manager.RealAuthListSearchVO;

@RestController
@RequestMapping("/user/realAuth")
@Api(tags = "用户管理")
public class UserRealAuthController extends BaseController {
	
	@Resource
	private UserRealAuthInfoService userRealAuthInfoService;
	
	@ApiOperation("实名审核")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserRealAuthInfo>> list(@RequestBody RealAuthListSearchVO param) {
		Page<UserRealAuthInfo> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<UserRealAuthInfo> lqw = new LambdaQueryWrapper<>();
		if(param.getUserId() != null && param.getUserId() > 0) {
			lqw.eq(UserRealAuthInfo::getUserId, param.getUserId());
		}
		if(param.getRequestTimeStart() != null) {
			lqw.ge(UserRealAuthInfo::getRequestTime, param.getRequestTimeStart());
		}
		if(param.getRequestTimeEnd() != null) {
			lqw.le(UserRealAuthInfo::getRequestTime, param.getRequestTimeEnd());
		}
		if(param.getOperateTimeStart() != null) {
			lqw.ge(UserRealAuthInfo::getOperateTime, param.getOperateTimeStart());
		}
		if(param.getOperateTimeEnd() != null) {
			lqw.le(UserRealAuthInfo::getOperateTime, param.getOperateTimeEnd());
		}
		if(param.getRealAuthStatus() != null && param.getRealAuthStatus() > 0) {
			lqw.eq(UserRealAuthInfo::getRealAuthStatus, param.getRealAuthStatus());
		}
		lqw.orderByDesc(UserRealAuthInfo::getId);
		userRealAuthInfoService.page(page, lqw);
		return Response.successData(page);
	}
	
	@ApiOperation("实名审核-证件信息")
	@PostMapping("/certificate")
	@ResponseBody
	public Response<UserRealAuthInfo> certificate(@ApiParam("记录ID") @RequestParam("id") Integer id) {
		UserRealAuthInfo ura = userRealAuthInfoService.getById(id);
		if(ura == null) {
			return Response.fail("记录不存在");
		}
		return Response.successData(ura);
	}
	
	@ApiOperation("实名审核-认证状态修改")
	@PostMapping("/updateRealAuthStatus")
	@ResponseBody
	public Response<Void> updateRealAuthStatus(
			@ApiParam("记录ID") @RequestParam("id") Integer id, 
			@ApiParam("操作状态，2-通过,3-未审核通过") @RequestParam(value = "realAuthStatus") Integer realAuthStatus) {
		return userRealAuthInfoService.managerUpdateRealAuthStatus(id, realAuthStatus, getIp(), getUser().getOperator());
	}
}
