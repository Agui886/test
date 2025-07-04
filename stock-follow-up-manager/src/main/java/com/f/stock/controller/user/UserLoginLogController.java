package com.f.stock.controller.user;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserLoginLog;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserLoginLogService;
import utils.StringUtil;
import vo.manager.UserLoginLogParamVO;

@RestController
@RequestMapping("/user/log")
@Api(tags = "用户管理")
public class UserLoginLogController {
	
	@Resource
	private UserLoginLogService userLoginLogService;
	
	@ApiOperation("会员登录日志")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserLoginLog>> list(@RequestBody UserLoginLogParamVO param) {
		Page<UserLoginLog> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<UserLoginLog> lqw = new LambdaQueryWrapper<>();
		if(param.getUserId() != null && param.getUserId() > 0) {
			lqw.eq(UserLoginLog::getUserId, param.getUserId());
		}
		if(!StringUtil.isEmpty(param.getIp())) {
			lqw.eq(UserLoginLog::getIp, param.getIp());
		}
		if(param.getStartTime() != null) {
			lqw.ge(UserLoginLog::getOperateTime, param.getStartTime());
		}
		if(param.getEndTime() != null) {
			lqw.le(UserLoginLog::getOperateTime, param.getEndTime());
		}
		lqw.orderByDesc(UserLoginLog::getOperateTime);
		userLoginLogService.page(page, lqw);
		return Response.successData(page);
	}
}
