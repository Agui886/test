package com.f.stock.controller.user;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponseBody.ResultObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import annotation.NotNeedAuth;
import annotation.RequestLimit;
import entity.SiteInternalMessage;
import entity.UserAmtChangeRecord;
import entity.UserDataChangeRecord;
import entity.UserInfo;
import entity.UserRealAuthInfo;
import entity.common.Response;
import enums.UserDataChangeTypeEnum;
import enums.UserRealAuthStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mapper.UserInfoMapper;
import service.CountryService;
import service.IpAddressService;
import service.SiteInternalMessageService;
import service.UserAmtChangeRecordService;
import service.UserInfoService;
import service.UserRealAuthInfoService;
import utils.IPUtil;
import utils.RedisDao;
import utils.StringUtil;
import utils.aliyun.AliyunFaceUtil;
import vo.common.TokenUserVO;
import vo.manager.UserAmtChangeRecordSearchParamVO;
import vo.server.AliyunInitFaceVerifyParamVO;
import vo.server.MeVO;
import vo.server.MyInfoVO;
import vo.server.RegisterParamVO;

@Controller
@RequestMapping("/user")
@Api(tags = "我的")
public class UserController extends BaseController {

	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserInfoMapper userInfoMapper;

	@Resource
	private RedisDao redisDao;

	@Resource
	private CountryService countryService;
	
	@Resource
	private SiteInternalMessageService siteInternalMessageService;
	
	@Resource
	private UserRealAuthInfoService userRealAuthInfoService;
	
	@Resource
	private UserAmtChangeRecordService userAmtChangeRecordService;
	
	@Resource
	private IpAddressService ipAddressService;

	@ApiOperation("注册验证")
	@PostMapping("/registerVerify")
	@ResponseBody
	@RequestLimit
	@NotNeedAuth
	public Response<Void> registerVerify(@RequestBody RegisterParamVO param) {
		return userInfoService.registerVerify(param);
	}
	
	@ApiOperation("注册")
	@PostMapping("/register")
	@ResponseBody
	@RequestLimit
	@NotNeedAuth
	public Response<Void> register(@RequestBody RegisterParamVO param) {
		String ip = IPUtil.getIpAddr(httpServletRequest);
		return userInfoService.register(param, ip);
	}

	@ApiOperation("登录")
	@PostMapping("/login")
	@ResponseBody
	@RequestLimit
	@NotNeedAuth
	public Response<TokenUserVO> login(
			@ApiParam("地区编号，手机号登录时不能为空，如：+86") @RequestParam(value = "areaCode", required = false) String areaCode,
			@ApiParam("登录账号（手机号或登录id或邮箱）") @RequestParam("loginAccount") String loginAccount,
			@ApiParam("登录密码") @RequestParam("loginPwd") String loginPwd,
			@ApiParam("登录方式, 0-登录id和密码，1-手机号和登录密码，2-邮箱和密码") @RequestParam(value = "loginType", defaultValue = "0", required = true) int loginType) {
		String ip = IPUtil.getIpAddr(httpServletRequest);
		String requestUrl = IPUtil.getRequestURL(httpServletRequest);
		return userInfoService.login(areaCode, loginAccount, loginPwd, loginType, ip, requestUrl);
	}
	
	@ApiOperation("登出")
	@PostMapping("/logout")
	@ResponseBody
	@RequestLimit
	public Response<Void> logout() {
		String ip = IPUtil.getIpAddr(httpServletRequest);
		String requestUrl = IPUtil.getRequestURL(httpServletRequest);
		TokenUserVO tu = super.getTokenUser();
		return userInfoService.logout(tu.getLoginId(), ip, requestUrl);
	}

	@ApiOperation("我的账号信息，可用于刷新余额信息和认证状态")
	@PostMapping("/me")
	@ResponseBody
	public Response<MeVO> me() {
		TokenUserVO tu = super.getTokenUser();
		UserInfo ui = this.userInfoService.getById(tu.getLoginId());
		MeVO me = new MeVO();
		me.setLoginId(ui.getId());
		me.setAreaCode(ui.getAreaCode());
		me.setPhone(ui.getPhone());
		me.setEmail(ui.getEmail());
		me.setAccountType(ui.getAccountType());
		me.setAvailableAmt(ui.getAvailableAmt());
		BigDecimal unavailableWithdrawalAmt = userInfoMapper.getUserUnavailableWithdrawalAmt(tu.getLoginId());
		if(unavailableWithdrawalAmt.compareTo(ui.getAvailableAmt()) == 1) {
			unavailableWithdrawalAmt = ui.getAvailableAmt();
		}
		me.setAvailableWithdrawalAmt(ui.getAvailableAmt().subtract(unavailableWithdrawalAmt));
		me.setDetentionAmt(ui.getDetentionAmt());
		me.setFundingStatus(ui.getFundingStatus());
		me.setFollowAmt(ui.getFollowAmt());
		me.setNickname(ui.getNickname());
		me.setRealAuthStatus(ui.getRealAuthStatus());
		me.setTradingFrozenAmt(ui.getTradingFrozenAmt());
		me.setHasFundPwd(StringUtil.isEmpty(ui.getFundPwd()) ? false : true);
		me.setUnReadMessages(siteInternalMessageService.lambdaQuery().eq(SiteInternalMessage::getUserId, tu.getLoginId()).eq(SiteInternalMessage::getIsRead, false).count());
		if(ui.getRealAuthStatus() == UserRealAuthStatusEnum.REVIEWED.getCode()) {
			UserRealAuthInfo ra = userRealAuthInfoService.lambdaQuery()
					.eq(UserRealAuthInfo::getUserId, ui.getId())
					.eq(UserRealAuthInfo::getRealAuthStatus, UserRealAuthStatusEnum.REVIEWED.getCode()).one();
			if (ra != null) {
				me.setRequestTime(ra.getRequestTime());
				me.setSignature(ra.getSignature());
			}
		}
		me.setLevelId(ui.getLevelId());
		me.setPromotionCode(ui.getPromotionCode()); 
		me.setPromotionEnable(ui.getPromotionEnable());
		return Response.successData(me);
	}
	
	@ApiOperation("我的个人信息")
	@PostMapping("/myInfo")
	@ResponseBody
	public Response<MyInfoVO> myInfo() {
		TokenUserVO tu = super.getTokenUser();
		UserInfo ui = this.userInfoService.getById(tu.getLoginId());
		MyInfoVO me = new MyInfoVO();
		me.setUid(ui.getId());
		me.setNickname(ui.getNickname());
		me.setRealAuthStatus(ui.getRealAuthStatus());
		me.setFundingStatus(ui.getFundingStatus());
		me.setPromotionCode(ui.getPromotionCode());
		me.setRegInvitationCode(ui.getRegInvitationCode());
		me.setCertificateNumber(ui.getCertificateNumber());
		me.setRealName(ui.getRealName());
		me.setCertificateType(ui.getCertificateType());
		return Response.successData(me);
	}
	
	
	@ApiOperation("客服链接")
	@PostMapping("/serviceUrl")
	@ResponseBody
	public Response<String> serviceUrl() {
		TokenUserVO tu = super.getTokenUser();
		UserInfo ui = userInfoService.getById(tu.getLoginId());
		//AgentInfo ai = agentInfoService.getById(ui.getAgentId());
		String url = "";
//		if(ai != null) {
//			url = ai.getServiceUrl();
//		}
		return Response.successData(url);
	}
	
	@ApiOperation("反馈意见")
	@PostMapping("/feedback")
	@ResponseBody
	public Response<Void> feedback(@ApiParam("反馈内容") @RequestParam("content") String content) {
		return Response.success();
	}
	
	@ApiOperation("阿里云-发起人脸认证")
	@PostMapping("/aliyunInitFaceVerify")
	@ResponseBody
	@NotNeedAuth
	public Response<ResultObject> aliyunInitFaceVerify(@RequestBody AliyunInitFaceVerifyParamVO param) {
		LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
		switch (param.getVerifyType()) {
		case 0://注册人脸认证(未注册用户，注册+人脸认证)
			param.setIp(IPUtil.getIpAddr(httpServletRequest));
			param.setRequestUrl( IPUtil.getRequestURL(httpServletRequest));
			break;
			
		case 1://忘记密码(手机号-人脸认证,通过手机号，获取用户人脸认证)
			if(StringUtil.isEmpty(param.getAreaCode())) {
				return Response.fail("请选择地区编号");
			}
			if(!StringUtil.isNumber(param.getAccount())) {
				return Response.fail("手机号码格式错误，请重新输入");
			}
			lqw.eq(UserInfo::getAreaCode, param.getAreaCode());
			lqw.eq(UserInfo::getPhone, param.getAccount());
			break;
			
		case 2://忘记密码(邮箱-人脸认证,通过邮箱，获取用户人脸认证)
			if(!StringUtil.isEmail(param.getAccount())) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			lqw.eq(UserInfo::getEmail, param.getAccount());
			break;

		default://用户人脸认证(已注册用户，当前token用户人脸认证)
			TokenUserVO tu = super.getTokenUser();
			if (tu == null) {
				return Response.fail("用户未登录！");
			}
			lqw.eq(UserInfo::getId, tu.getLoginId());
			break;
		}
		if(param.getVerifyType() != 0) {
			//获取用户对象
			UserInfo me = userInfoService.getOne(lqw);
			//设置用户信息
			param.setUserInfo(me);
		}
		//执行阿里云-发起人脸认证
		return userInfoService.doAliyunInitFaceVerify(param);
	}
	
	@ApiOperation("阿里云-获取人脸认证结果")
	@PostMapping("/aliyunDescribeFaceVerify")
	@ResponseBody
	@NotNeedAuth
	public Response<String> aliyunDescribeFaceVerify(@ApiParam("实人认证唯一标识") @RequestParam("certifyId") String certifyId){
		return userInfoService.doAliyunDescribeFaceVerify(certifyId);
	}
	
	/**
	 * 阿里云-实人认证请求-回调
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	@GetMapping(value = "aliyunInitFaceVerifyCallback")
	@NotNeedAuth
	public void aliyunInitFaceVerifyCallback(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("====================金融级活体检测-实人认证请求-回调进入====================");
		try {
			TreeMap<String, String> params = AliyunFaceUtil.getParams(request);
			System.out.println("====================金融级活体检测-实人认证请求-回调内容："+params+"====================");
			if(params.get("certifyId") != null && params.get("passed") != null) {
				userInfoService.doFaceVerifyBusiness(params.get("certifyId"),params.get("passed"));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			response.setCharacterEncoding("UTF-8");
			response.getOutputStream().write("200".getBytes());
			response.flushBuffer();
		}
	}
	
	@ApiOperation("忘记密码(人脸认证-修改密码)")
	@PostMapping("/faceVerifyModifyLoginPwd")
	@ResponseBody
	@NotNeedAuth
	public Response<Void> faceVerifyModifyLoginPwd(
			@ApiParam("实人认证唯一标识") @RequestParam("certifyId") String certifyId,
			@ApiParam("新登录密码") @RequestParam("newLoginPwd") String newLoginPwd){
		return userInfoService.doFaceVerifyModifyLoginPwd(certifyId, newLoginPwd,super.getIp());
	}
	
	@ApiOperation("资金记录")
	@PostMapping("/userAmtChangeRecord")
	@ResponseBody
	public Response<Page<UserAmtChangeRecord>> userAmtChangeRecord(@RequestBody UserAmtChangeRecordSearchParamVO param) {
		param.setUserId(super.getTokenUser().getLoginId());
		Page<UserAmtChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		userAmtChangeRecordService.managerList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("修改昵称")
	@PostMapping("/updateNickname")
	@ResponseBody
	public Response<Void> updateNickname(@RequestParam("newNickname") String newNickname) {
		if(StringUtil.isEmpty(newNickname)) {
			return Response.fail("请输入新的昵称");
		}
		TokenUserVO tu = super.getTokenUser();
		UserInfo me = this.userInfoService.getById(tu.getLoginId());
		if(me.getNickname().equals(newNickname)) {
			return Response.fail("请输入新的昵称");
		}
		String ip = super.getIp();
		new UserInfo().setId(me.getId()).setNickname(newNickname).updateById();
		UserDataChangeRecord r = new UserDataChangeRecord();
		r.setUserId(me.getId());
		r.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		r.setDataChangeTypeName(UserDataChangeTypeEnum.EDIT_USER.getName());
		r.setOldContent("昵称：" + me.getNickname());
		r.setOldContent("昵称：" + newNickname);
		r.setIp(ip);
		r.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		r.setOperator(tu.getOperator());
		r.insert();
		return Response.success();
	}
}
