package com.f.stock.controller.agent;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import redis.RedisKeyPrefix;
import utils.RedisDao;
import vo.common.AgentConfig;
import vo.common.AgentConfig.Level2;
import vo.common.AgentConfig.Level3;
import vo.common.AgentConfig.Level4;
import vo.common.AgentConfig.Level5;

@RestController
@RequestMapping("/agent")
@Api(tags = "代理管理")
public class AgentConfigController {
	
	@Resource
	private RedisDao redisDao;
	
	@ApiOperation("代理配置详情")
	@PostMapping("/config")
	@ResponseBody
	public Response<AgentConfig> config() {
		AgentConfig c = redisDao.getBean(RedisKeyPrefix.getAgentConfigKey(), AgentConfig.class);
		return Response.successData(c);
	}
	
	@ApiOperation("代理配置保存")
	@PostMapping("/config/save")
	@ResponseBody
	public Response<Void> configSave(@RequestBody AgentConfig param) {
		if(param.getLevel2() == null) {
			return Response.fail("L2等级配置不能为空");
		}
		if(param.getLevel3() == null) {
			return Response.fail("L3等级配置不能为空");
		}
		if(param.getLevel4() == null) {
			return Response.fail("L4等级配置不能为空");
		}
		if(param.getLevel5() == null) {
			return Response.fail("L5等级配置不能为空");
		}
		BigDecimal Zero = BigDecimal.ZERO;
		Level2 l2 = param.getLevel2();
		Level3 l3 = param.getLevel3();
		Level4 l4 = param.getLevel4();
		Level5 l5 = param.getLevel5();
		/**从L1获取返佣比例配置检查**/
		if(l2.getRebateFromL1() == null || l2.getRebateFromL1().compareTo(Zero) < 0) {
			return Response.fail("L2从L1获得返佣比例不能为空或小于0");
		}
		if(l3.getRebateFromL1() == null || l3.getRebateFromL1().compareTo(Zero) < 0) {
			return Response.fail("L3从L1获得返佣比例不能为空或小于0");
		}
		if(l4.getRebateFromL1() == null || l4.getRebateFromL1().compareTo(Zero) < 0) {
			return Response.fail("L4从L1获得返佣比例不能为空或小于0");
		}
		if(l5.getRebateFromL1() == null || l5.getRebateFromL1().compareTo(Zero) < 0) {
			return Response.fail("L5从L1获得返佣比例不能为空或小于0");
		}
		/**从L1获取返佣比例配置检查 End**/
		
		/**从L2获取返佣比例配置检查**/
		if(l3.getRebateFromL2() == null || l3.getRebateFromL2().compareTo(Zero) < 0) {
			return Response.fail("L3从L2获得返佣比例不能为空或小于0");
		}
		if(l4.getRebateFromL2() == null || l4.getRebateFromL2().compareTo(Zero) < 0) {
			return Response.fail("L4从L2获得返佣比例不能为空或小于0");
		}
		if(l5.getRebateFromL2() == null || l5.getRebateFromL2().compareTo(Zero) < 0) {
			return Response.fail("L5从L2获得返佣比例不能为空或小于0");
		}
		/**从L2获取返佣比例配置检查 End**/
		
		/**从L3获取返佣比例配置检查**/
		if(l4.getRebateFromL3() == null || l4.getRebateFromL3().compareTo(Zero) < 0) {
			return Response.fail("L4从L3获得返佣比例不能为空或小于0");
		}
		if(l5.getRebateFromL3() == null || l5.getRebateFromL3().compareTo(Zero) < 0) {
			return Response.fail("L5从L3获得返佣比例不能为空或小于0");
		}
		/**从L3获取返佣比例配置检查 End**/
		
		/**从L4获取返佣比例配置检查**/
		if(l5.getRebateFromL4() == null || l5.getRebateFromL4().compareTo(Zero) < 0) {
			return Response.fail("L5从L4获得返佣比例不能为空或小于0");
		}
		/**从L4获取返佣比例配置检查 End**/
		
		/**升级/维持推广人数配置检查**/
		if(l2.getMemberCountOfL1() == null  || l2.getMemberCountOfL1() < 1) {
			return Response.fail("L2推广L1会员人数不能为空或小于1");
		}
		if(l3.getMemberCountOfL1() == null  || l3.getMemberCountOfL1() < 1) {
			return Response.fail("L3推广L1会员人数不能为空或小于1");
		}
		if(l4.getMemberCountOfL1() == null  || l4.getMemberCountOfL1() < 1) {
			return Response.fail("L4推广L1会员人数不能为空或小于1");
		}
		if(l5.getMemberCountOfL1() == null  || l5.getMemberCountOfL1() < 1) {
			return Response.fail("L5推广L1会员人数不能为空或小于1");
		}
		if(l3.getMemberCountOfL2() == null  || l3.getMemberCountOfL2() < 1) {
			return Response.fail("L3推广L2会员人数不能为空或小于1");
		}
		if(l4.getMemberCountOfL3() == null  || l4.getMemberCountOfL3() < 1) {
			return Response.fail("L4推广L3会员人数不能为空或小于1");
		}
		if(l5.getMemberCountOfL4() == null  || l5.getMemberCountOfL4() < 1) {
			return Response.fail("L5推广L4会员人数不能为空或小于1");
		}
		/**升级/维持推广人数配置检查 End**/
		
		/**升级/维持个人当月业绩配置检查**/
		if(l2.getPersMonthlyPerformance() == null  || l2.getPersMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L2个人当月业绩不能为空或小于等于0");
		}
		if(l3.getPersMonthlyPerformance() == null  || l3.getPersMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L3个人当月业绩不能为空或小于等于0");
		}
		if(l4.getPersMonthlyPerformance() == null  || l4.getPersMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L4个人当月业绩不能为空或小于等于0");
		}
		if(l5.getPersMonthlyPerformance() == null  || l5.getPersMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L5个人当月业绩不能为空或小于等于0");
		}
		/**升级/维持个人当月业绩配置检查 End**/
		
		/**升级/维持团队当月业绩配置检查**/
		if(l2.getTeamMonthlyPerformance() == null  || l2.getTeamMonthlyPerformance().compareTo(Zero) < 0) {
			return Response.fail("L2团队当月业绩不能为空或小于0");
		}
		if(l3.getTeamMonthlyPerformance() == null  || l3.getTeamMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L3团队当月业绩不能为空或小于等于0");
		}
		if(l4.getTeamMonthlyPerformance() == null  || l4.getTeamMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L4团队当月业绩不能为空或小于等于0");
		}
		if(l5.getTeamMonthlyPerformance() == null  || l5.getTeamMonthlyPerformance().compareTo(Zero) <= 0) {
			return Response.fail("L5团队当月业绩不能为空或小于等于0");
		}
		/**升级/维持团队当月业绩配置检查 End**/
		
		/**升级/维持团队规模配置检查 **/
		if(l4.getTeamMemberCount() == null  || l4.getTeamMemberCount() < 1) {
			return Response.fail("L4团队总人数不能为空或小于1");
		}
		if(l5.getTeamMemberCount() == null  || l5.getTeamMemberCount() < 1) {
			return Response.fail("L5团队总人数不能为空或小于1");
		}
		/**升级/维持团队规模配置检查 End**/
		
		/**升级/维持团队结构配置检查 **/
		if(l3.getAgentCountOfL2() == null  || l3.getAgentCountOfL2() < 1) {
			return Response.fail("L3团队结构L2代理人数不能为空或小于1");
		}
		if(l4.getAgentCountOfL2() == null  || l4.getAgentCountOfL2() < 1) {
			return Response.fail("L4团队结构L2代理人数不能为空或小于1");
		}
		if(l5.getAgentCountOfL2() == null  || l5.getAgentCountOfL2() < 1) {
			return Response.fail("L5团队结构L2代理人数不能为空或小于1");
		}
		if(l4.getAgentCountOfL3() == null  || l4.getAgentCountOfL3() < 1) {
			return Response.fail("L4团队结构L3代理人数不能为空或小于1");
		}
		if(l5.getAgentCountOfL3() == null  || l5.getAgentCountOfL3() < 1) {
			return Response.fail("L5团队结构L3代理人数不能为空或小于1");
		}
		if(l5.getAgentCountOfL4() == null  || l5.getAgentCountOfL4() < 1) {
			return Response.fail("L5团队结构L4代理人数不能为空或小于1");
		}
		/**升级/维持团队结构配置检查 End**/
		
		if(param.getRebateSettlementCycle() == null || param.getRebateSettlementCycle() < 0 || param.getRebateSettlementCycle() > 3) {
			return Response.fail("请选择返佣结算周期");
		}
		
		if(param.getLevelSettlementCycle() == null || param.getRebateSettlementCycle() < 1 || param.getRebateSettlementCycle() > 3) {
			return Response.fail("请选择升降级结算周期");
		}
		redisDao.setBean(RedisKeyPrefix.getAgentConfigKey(), param);
		return Response.success();
	}
}
