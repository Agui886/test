package com.f.stock.controller.user.setting;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.f.stock.controller.BaseController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserInfoService;
import vo.common.TokenUserVO;

@Controller
@RequestMapping("/user/setting")
@Api(tags = "我的")
public class SecuritySettingController extends BaseController  {
	
	@Resource
	private UserInfoService userInfoService;
	
	@ApiOperation("设置-安全设置-修改手机号码")
	@PostMapping("/modifyPhone")
	@ResponseBody
	public Response<Void> modifyPhone(
			@ApiParam("地区编码，如+86") @RequestParam("areaCode") String areaCode,
			@ApiParam("手机号码") @RequestParam("phone") String phone
			) {
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.modifyPhone(tu.getLoginId(), areaCode, phone, super.getIp(), tu.getOperator());
	}
	
	@ApiOperation("设置-安全设置-修改邮箱")
	@PostMapping("/modifyEmail")
	@ResponseBody
	public Response<Void> modifyEmail(@ApiParam("邮箱") @RequestParam("email") String email) {
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.modifyEmail(tu.getLoginId(), email, super.getIp(), tu.getOperator());
	}
	
	@ApiOperation("设置-安全设置-修改登录密码")
	@PostMapping("/modifyLoginPwd")
	@ResponseBody
	public Response<Void> modifyLoginPwd(
			@ApiParam("旧登录密码") @RequestParam("oldLoginPwd") String oldLoginPwd,
			@ApiParam("新登录密码") @RequestParam("newLoginPwd") String newLoginPwd
			) {
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.modifyLoginPwd(tu.getLoginId(), oldLoginPwd, newLoginPwd, super.getIp(), tu.getOperator());
	}
	
	@ApiOperation("设置-安全设置-修改交易密码")
	@PostMapping("/modifyFundPwd")
	@ResponseBody
	public Response<Void> modifyFundPwd(
			@ApiParam("旧密码，未设置交易密码，则验证登录密码；已设置则验证原交易密码") @RequestParam("oldPwd") String oldPwd,
			@ApiParam("新交易密码") @RequestParam("newFundPwd") String newFundPwd
			) {
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.modifyFundPwd(tu.getLoginId(), oldPwd, newFundPwd, super.getIp(), tu.getOperator());
	}
	
	@ApiOperation("设置-安全设置-密码验证")
	@PostMapping("/pwdVerify")
	@ResponseBody
	public Response<Void> pwdVerify(
			@ApiParam("密码") @RequestParam("pwd") String pwd,
			@ApiParam("类型：0-登录密码；1-交易密码；") @RequestParam("pwdType") int pwdType
			) {
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.pwdVerify(tu.getLoginId(), pwd, pwdType);
	}
}
