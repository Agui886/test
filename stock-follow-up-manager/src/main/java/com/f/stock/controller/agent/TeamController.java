package com.f.stock.controller.agent;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserInfoService;
import vo.manager.TeamDetailSearchVO;
import vo.manager.TeamRebateOrInviteSearchVO;

@RestController
@RequestMapping("/agent/team")
@Api(tags = "代理管理")
public class TeamController {
	
	@Resource
	private UserInfoService userInfoService;;
	
	@ApiOperation("代理团队查询-返佣人数查询")
	@PostMapping("/rebates")
	@ResponseBody
	public Response<TeamRebateOrInviteSearchVO> rebates(@RequestParam("userId") @ApiParam("会员id") Integer userId) {
		return userInfoService.teamRebates(userId);
	}
	
	@ApiOperation("代理团队查询-团队成员查询")
	@PostMapping("/detail")
	@ResponseBody
	public Response<TeamDetailSearchVO> detail(@RequestParam("userId") @ApiParam("会员id") Integer userId) {
		return userInfoService.teamDetail(userId);
	}
	
	@ApiOperation("代理团队查询-邀请人数查询")
	@PostMapping("/invite")
	@ResponseBody
	public Response<TeamRebateOrInviteSearchVO> invite(@RequestParam("userId") @ApiParam("会员id") Integer userId) {
		return userInfoService.invitedMembers(userId);
	}
}
