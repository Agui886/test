package com.f.stock.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import annotation.NotNeedAuth;
import annotation.RequestLimit;
import entity.SysMenu;
import entity.SysUser;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SysMenuService;
import service.SysUserService;
import vo.manager.SysUserLoginResponseVO;

@RestController
@Api(tags = "管理员登录")
public class LoginController extends BaseController {

	@Resource
	private SysUserService sysUserService;
	
	@Resource
	private SysMenuService sysMenuService;
	
	@ApiOperation("登录")
	@NotNeedAuth
	@PostMapping("/login")
	@RequestLimit
	public Response<SysUserLoginResponseVO> login(
			@ApiParam("管理员账号") @RequestParam("username") String username, 
			@ApiParam("登录密码") @RequestParam("loginPwd") String loginPwd,
			@ApiParam("安全码") @RequestParam(value = "code", required = false) String code) {
		String ip = super.getIp();
		return sysUserService.login(username, loginPwd, code, ip);
	}
	
	@ApiOperation("登出")
	@PostMapping("/logout")
	@RequestLimit
	public Response<Void> logout() {
		return sysUserService.logout(getToken(), getUser().getId());
	}
	
	@ApiOperation("账号信息")
	@PostMapping("/myInformation")
	@RequestLimit
	public Response<SysUserLoginResponseVO> myInformation() {
		SysUserLoginResponseVO vo = new SysUserLoginResponseVO();
		SysUser user = getUser();
		vo.setUsername(user.getUsername());
		vo.setRealName(user.getRealName());
		//vo.setToken(getToken());
		return Response.successData(vo);
	}
	
	@ApiOperation("获取用户权限")
    @PostMapping(value = "/getMyMenus")
    public Response<List<SysMenu>> getMyMenus(){
        SysUser user = getUser();
        List<SysMenu> list = sysMenuService.selectAllMenuIdByUserId(user.getId(), null, true);
        return Response.successData(list);
    }
}
