 package com.f.stock.controller.agent;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import cn.hutool.core.date.DateUtil;
import entity.UserInfo;
import entity.UserLevelChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import mapper.UserInfoMapper;
import service.UserInfoService;
import service.UserLevelChangeRecordService;
import service.UserRebateDetailService;
import utils.StringUtil;
import vo.common.TokenUserVO;
import vo.manager.RebateStatisticsVO.RebateStatistics;
import vo.manager.TeamMembers;
import vo.manager.UserLevelChangeRecordParamVO;
import vo.server.AgentRebateStatisticsVO;
import vo.server.AgentStatisticsVO;
import vo.server.UserRebateDetailParamVO;
import vo.server.UserRebateDetailVO;

@RestController
@RequestMapping("/agencyCenter")
@Api(tags = "代理中心")
public class AgentController extends BaseController{
	
	@Resource
	private UserRebateDetailService userRebateDetailService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private UserInfoMapper userInfoMapper;
	
	@Resource
	private UserLevelChangeRecordService userLevelChangeRecordService;

	@ApiOperation("我的-资产-代理中心-统计信息")
	@PostMapping("/statistics")
	@ResponseBody
	public Response<AgentStatisticsVO> statistics() {
		UserInfo userInfo = userInfoService.getById(super.getTokenUser().getLoginId());
		if (userInfo == null) {
			return Response.fail("用户不存在");
		}
		AgentStatisticsVO agentStatisticsVO = new AgentStatisticsVO();
		//1、根据用户id，获取总的统计（累计返佣金额、贡献用户总数、贡献用户当时总业绩）
		agentStatisticsVO.setRebateStatistics(userRebateDetailService.getRebateStatistics(userInfo.getId(),null,null,null));
		//2、根据用户id，获取上月返佣总金额
		Date nowTime = new Date();//获取当前时间
		Date endTime = DateUtil.beginOfMonth(nowTime);// 获取当前月的第一天0点（作为结束时间）
		Date starTime = DateUtil.offsetMonth(endTime, -1);// 计算上个月的第一天0点（开始时间）
		//上月总统计
		RebateStatistics ultRebateStatistics = userRebateDetailService.getRebateStatistics(userInfo.getId(),starTime,endTime,null);
		//上月返佣金额
		agentStatisticsVO.setUltRrebateAmount(ultRebateStatistics.getTotalRebateAmount());
		//3、累计邀请人数(查询邀请码是自己的推广码，总数)
		if (StringUtil.isNotEmpty(userInfo.getPromotionCode())) {
			Integer accumulativeRegInvitationNum = userInfoService.lambdaQuery()
					.eq(UserInfo :: getRegInvitationCode, userInfo.getPromotionCode())
					.count();
			agentStatisticsVO.setAccumulativeRegInvitationNum(accumulativeRegInvitationNum);
		}
		//4、团队总人数（不含自己）
		Integer memberCount = userInfoService.lambdaQuery()
				.eq(UserInfo :: getGeneralAgentId, userInfo.getId())
				.count();
		agentStatisticsVO.setMemberCount(memberCount);
		//5、直属会员
		Integer underlingUserNum = userInfoService.lambdaQuery()
		.eq(UserInfo :: getAgentId, userInfo.getId())
		.count();
		agentStatisticsVO.setUnderlingUserNum(underlingUserNum);
		//6、本月新增团队人数
		Integer curMemberCount = userInfoService.lambdaQuery()
				.eq(UserInfo :: getGeneralAgentId, userInfo.getId())
				.ge(UserInfo :: getRegTime,endTime)//大于等于,当前月的第一天0点（作为结束时间）
				.le(UserInfo :: getRegTime,nowTime)//小于等于,当前时间
				.count();
		agentStatisticsVO.setCurMemberCount(curMemberCount);
		//7、本月新增直属
		Integer curUnderlingUserNum = userInfoService.lambdaQuery()
				.eq(UserInfo :: getAgentId, userInfo.getId())
				.ge(UserInfo :: getRegTime,endTime)//大于等于,当前月的第一天0点（作为结束时间）
				.le(UserInfo :: getRegTime,nowTime)//小于等于,当前时间
				.count();
		agentStatisticsVO.setCurUnderlingUserNum(curUnderlingUserNum);
		return Response.successData(agentStatisticsVO);
	}
	
	@ApiOperation("我的-资产-代理中心-返佣明细（返佣统计）")
	@PostMapping("/rebateStatistics")
	@ResponseBody
	public Response<AgentRebateStatisticsVO> rebateStatistics(
			@ApiParam("开始时间") @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
			@ApiParam("结束时间") @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
		AgentRebateStatisticsVO agentRebateStatisticsVO = new AgentRebateStatisticsVO();
		agentRebateStatisticsVO.setRebateStatistics(userRebateDetailService.getRebateStatistics(super.getTokenUser().getLoginId(),startTime,endTime,null));
		agentRebateStatisticsVO.setRebateStatisticsLevel1(userRebateDetailService.getRebateStatistics(super.getTokenUser().getLoginId(),startTime,endTime,1));
		agentRebateStatisticsVO.setRebateStatisticsLevel2(userRebateDetailService.getRebateStatistics(super.getTokenUser().getLoginId(),startTime,endTime,2));
		agentRebateStatisticsVO.setRebateStatisticsLevel3(userRebateDetailService.getRebateStatistics(super.getTokenUser().getLoginId(),startTime,endTime,3));
		agentRebateStatisticsVO.setRebateStatisticsLevel4(userRebateDetailService.getRebateStatistics(super.getTokenUser().getLoginId(),startTime,endTime,4));
		agentRebateStatisticsVO.setRebateStatisticsLevel5(userRebateDetailService.getRebateStatistics(super.getTokenUser().getLoginId(),startTime,endTime,5));
		return Response.successData(agentRebateStatisticsVO);
	}
	
	@ApiOperation("我的-资产-代理中心-返佣明细-会员返佣详情")
	@PostMapping("/userRebateDetail")
	@ResponseBody
	public Response<UserRebateDetailVO> userRebateDetail(@RequestBody UserRebateDetailParamVO param) {
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		return userRebateDetailService.userRebateDetail(param);
	}
	
	@ApiOperation("我的-资产-代理中心-团队详情")
	@PostMapping("/agentTeamMembers")
	@ResponseBody
	public Response<List<TeamMembers>> agentTeamMembers(
			@ApiParam("开始时间") @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
			@ApiParam("结束时间") @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
		List<TeamMembers> tms = userInfoMapper.teamMembers(super.getTokenUser().getLoginId(),startTime,endTime);
		if(tms == null || tms.size() == 0) {
			return Response.fail("没有数据");
		}
		return Response.successData(tms);
	}
	
	@ApiOperation("我的-资产-代理中心-升降级记录")
	@PostMapping("/userLevelChangeRecord")
	@ResponseBody
	public Response<Page<UserLevelChangeRecord>> userLevelChangeRecord(@RequestBody UserLevelChangeRecordParamVO param) {
		param.setUserId(super.getTokenUser().getLoginId());
		Page<UserLevelChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		userLevelChangeRecordService.managerList(page,param);
		return Response.successData(page);
	}

}
