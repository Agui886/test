package com.f.stock.controller.user;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.UserBankInfo;
import entity.UserInfo;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mapper.UserInfoMapper;
import service.UserBankInfoService;
import service.UserInfoService;
import vo.manager.AddUserParamVO;
import vo.manager.EditUserParamVO;
import vo.manager.UserListDetailVO;
import vo.manager.UserListSearchParamVO;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController extends BaseController {

	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserInfoMapper userInfoMapper;
	
	@Resource
	private UserBankInfoService userBankInfoService;
	
	
	@ApiOperation("用户列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserInfo>> list(@RequestBody UserListSearchParamVO vo) {
		Page<UserInfo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
		userInfoService.managerUserList(page, vo);
		return Response.successData(page);
	}
	
	@ApiOperation("用户列表-添加用户")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody AddUserParamVO vo) {
		return userInfoService.managerUserAdd(vo, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("用户列表-用户详情")
	@PostMapping("/detail")
	@ResponseBody
	public Response<UserListDetailVO> detail(@ApiParam("用户ID") @RequestParam("userId") Integer userId) {
		UserInfo user = userInfoService.getById(userId);
		if(user == null) {
			return Response.fail("用户信息错误");
		}
		UserListDetailVO vo = new UserListDetailVO();
		vo.setId(user.getId());
		vo.setNickname(user.getNickname());
		vo.setAreaCode(user.getAreaCode());
		vo.setPhone(user.getPhone());
		vo.setEmail(user.getEmail());
		vo.setAccountType(user.getAccountType());
		vo.setLoginEnable(user.getLoginEnable());
		vo.setFollowEnable(user.getFollowEnable());
		vo.setAgentId(user.getAgentId());
		if(user.getAgentId() > 0) {
			UserInfo agent = userInfoService.getById(user.getAgentId());
			vo.setAgentName(agent.getRealName());
		}
		vo.setRegTime(user.getRegTime());
		vo.setRegIp(user.getRegIp());
		vo.setRegAddress(user.getRegAddress());
		vo.setRealAuthStatus(user.getRealAuthStatus());
		vo.setFundingStatus(user.getFundingStatus());
		vo.setAvailableAmt(user.getAvailableAmt());
		vo.setTradingFrozenAmt(user.getTradingFrozenAmt());
		vo.setFollowAmt(user.getFollowAmt());
		BigDecimal unavailableWithdrawalAmt = userInfoMapper.getUserUnavailableWithdrawalAmt(userId);
		if(unavailableWithdrawalAmt.compareTo(user.getAvailableAmt()) == 1) {
			unavailableWithdrawalAmt = user.getAvailableAmt();
		}
		vo.setAvailableWithdrawalAmt(user.getAvailableAmt().subtract(unavailableWithdrawalAmt));
		vo.setDetentionAmt(user.getDetentionAmt());
		//增加真实姓名、证件号
		vo.setRealName(user.getRealName());
		vo.setCertificateNumber(user.getCertificateNumber());
		return Response.successData(vo);
	}
	
	@ApiOperation("用户列表-编辑用户")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody EditUserParamVO vo) {
		return userInfoService.managerUserEdit(vo, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("用户列表-银行卡信息")
	@PostMapping("/bankInfo")
	@ResponseBody
	public Response<UserBankInfo> bankInfo(@ApiParam("用户ID") @RequestParam("userId") Integer userId) {
		return Response.successData(userBankInfoService.lambdaQuery().eq(UserBankInfo::getUserId, userId).one());
	}
	
	@ApiOperation("用户列表-编辑银行卡")
	@PostMapping("/bankInfo/edit")
	@ResponseBody
	public Response<Void> bankInfoEdit(@RequestBody UserBankInfo info) {
		return userBankInfoService.edit(info, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("用户列表-强制下线")
	@PostMapping("/forcedOffline")
	@ResponseBody
	public Response<Void> forcedOffline(@ApiParam("用户ID") @RequestParam("userId") Integer userId) {
		return userInfoService.forcedOffline(userId);
	}
}
