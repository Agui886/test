package com.f.stock.controller.system;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.SysUserLoginLog;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.SysUserLoginLogService;
import utils.StringUtil;
import vo.manager.SysUserLoginLogSearchParamVO;

@RestController
@RequestMapping("/system/userLoginLog")
@Api(tags = "系统设置")
public class SysUserLoginLogController {
	
	@Resource
	private SysUserLoginLogService sysUserLoginLogService;
	
	@ApiOperation("管理员登录日志")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SysUserLoginLog>> list(@RequestBody SysUserLoginLogSearchParamVO param) {
		Page<SysUserLoginLog> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<SysUserLoginLog> lqw = new LambdaQueryWrapper<>();
		if(param.getSysUserId() != null && param.getSysUserId() > 0) {
			lqw.eq(SysUserLoginLog::getSysUserId, param.getSysUserId());
		}
		if(!StringUtil.isEmpty(param.getSysUserName())) {
			lqw.eq(SysUserLoginLog::getSysUserName, param.getSysUserName());
		}
		if(!StringUtil.isEmpty(param.getLoginIp())) {
			lqw.eq(SysUserLoginLog::getLoginIp, param.getLoginIp());
		}
		if(param.getStartTime() != null) {
			lqw.ge(SysUserLoginLog::getLoginTime, param.getStartTime());
		}
		if(param.getEndTime() != null) {
			lqw.le(SysUserLoginLog::getLoginTime, param.getEndTime());
		}
		lqw.orderByDesc(SysUserLoginLog::getId);
		sysUserLoginLogService.page(page, lqw);
		return Response.successData(page);
	}
}
