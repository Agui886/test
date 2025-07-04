package com.f.stock.controller.personal;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.f.stock.controller.BaseController;

import constant.SysConstant;
import entity.SysUser;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SysUserService;
import utils.PasswordGenerator;
import utils.StringUtil;

@RestController
@RequestMapping("/personal/setting")
@Api(tags = "个人中心")
public class PersonalController extends BaseController {
	
	@Resource
	private SysUserService sysUserService;
	
	@ApiOperation("个人设置-保存")
	@PostMapping("/save")
	@ResponseBody
	public Response<Void> save(@ApiParam("账号名称、昵称") @RequestParam("realName") String realName,
			@ApiParam("登录密码") @RequestParam(value = "loginPwd", required = false) String loginPwd) {
		LambdaUpdateWrapper<SysUser> luw = new LambdaUpdateWrapper<>();
		luw.eq(SysUser::getId, getUser().getId());
		luw.set(SysUser::getRealName, realName);
		if(!StringUtil.isEmpty(loginPwd)) {
			luw.set(SysUser::getLoginPwd, PasswordGenerator.generate(SysConstant.PASSWORD_PREFIX, loginPwd));
		}
		sysUserService.update(luw);
		return Response.success();
	}
}
