package com.f.stock.schema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import entity.AgentMemberInfo;
import entity.LevelDef;
import entity.UserInfo;
import entity.UserLevelChangeRecord;
import entity.UserRebateDetail;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;
import mapper.UserInfoMapper;
import redis.RedisKeyPrefix;
import service.AgentMemberInfoService;
import service.LevelDefService;
import service.UserFollowUpTradingService;
import service.UserInfoService;
import service.UserLevelChangeRecordService;
import service.UserRebateDetailService;
import service.UserStockClosingPositionService;
import service.UserStockPendingService;
import utils.RedisDao;
import vo.common.AgentConfig;
import vo.common.AgentConfig.Level2;
import vo.common.AgentConfig.Level3;
import vo.common.AgentConfig.Level4;
import vo.common.AgentConfig.Level5;

@Component
@Slf4j
public class LevelAndRebateSettlement {
	
	@Resource
	private UserRebateDetailService userRebateDetailService;
	
	@Resource
	private UserLevelChangeRecordService userLevelChangeRecordService;
	
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;
	
	@Resource
	private UserFollowUpTradingService userFollowUpTradingService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private LevelDefService levelDefService;
	
	@Resource
	private RedisDao redisDao;
	
	@Resource
	private UserInfoMapper userInfoMapper;
	
	@Resource
	private AgentMemberInfoService agentMemberInfoService;
	
	/**
	 * 返佣结算
	 * 
	 * @throws UnknownHostException
	 */
	@Scheduled(cron = "0 59 23 * * ?")
	//@Scheduled(fixedDelay = 60000)
	
	@Transactional
	@Async("stockTask")
	public void makeRebateSettlement() {
		log.info("============开始处理返佣结算==============");
		AgentConfig config = redisDao.getBean(RedisKeyPrefix.getAgentConfigKey(), AgentConfig.class);
		LocalDate ld = LocalDate.now();
		switch(config.getRebateSettlementCycle()) {
		case 0:
			log.info("返佣结算为实时结算，不用处理");
			return;
		case 2:
			DayOfWeek dayOfWeek = ld.getDayOfWeek();
			if(dayOfWeek != DayOfWeek.SUNDAY) {
				log.info("返佣结算为周结算，今天不是周日，不用处理");
				return;
			}
			break;
		case 3:
			if(ld.getDayOfMonth() != ld.lengthOfMonth()) {
				log.info("返佣结算为月结算，今天不是月末，不用处理");
				return;
			}
			break;
		}
		List<UserRebateDetail> list = userRebateDetailService.lambdaQuery().eq(UserRebateDetail::getIsSettlemented, false).isNull(UserRebateDetail::getRebateRatio).list();
		if(list.size() == 0)
			return;
		Date now = new Date();
		BigDecimal ZERO = BigDecimal.ZERO;
		list.forEach(i-> {
			BigDecimal rebateRadio = AgentConfig.getRebate(config, i.getUserLevelVal(), i.getFromUserLevelVal()).divide(new BigDecimal(100), 2, RoundingMode.DOWN);
			if(rebateRadio.compareTo(ZERO) > 0) {
				BigDecimal rebateAmount = i.getPerformance().multiply(rebateRadio);
				if(rebateAmount.compareTo(ZERO) > 0) {
					new UserRebateDetail()
					.setId(i.getId()).setRebateRatio(rebateRadio).setRebateAmount(rebateAmount).setSettlementDate(now).setSettlementDateTime(now).setIsSettlemented(true)
					.updateById();
					userInfoService.updateUserAvailableAmt(i.getUserId(), AmtDeTypeEnum.ReBateSettement, rebateAmount, "返佣结算，返佣记录id:" + i.getId(), CurrencyEnum.CNY, BigDecimal.ONE, "0.0.0.0", "系统", "系统",i.getTradingOrderSn());
				} else {
					new UserRebateDetail().setId(i.getId()).setRebateRatio(rebateRadio).setRebateAmount(ZERO).updateById();
				}
			} else {
				new UserRebateDetail().setId(i.getId()).setRebateRatio(ZERO).setRebateAmount(ZERO).updateById();
			}
		});
		log.info("============结束处理返佣结算==============");
	}
	
	
	/**
	 * 等级结算
	 * 
	 * @throws UnknownHostException
	 */
	@Scheduled(cron = "0 59 23 * * ?")
	//@Scheduled(fixedDelay = 60000)
	@Transactional
	@Async("stockTask")
	public void makeLevelSettlement() {
		log.info("============开始处理等级结算==============");
		AgentConfig config = redisDao.getBean(RedisKeyPrefix.getAgentConfigKey(), AgentConfig.class);
		LocalDate ld = LocalDate.now();
		switch(config.getLevelSettlementCycle()) {
		case 2:
			DayOfWeek dayOfWeek = ld.getDayOfWeek();
			if(dayOfWeek != DayOfWeek.SUNDAY) {
				log.info("等级结算为周结算，今天不是周日，不用处理");
				return;
			}
			break;
		case 3:
			if(ld.getDayOfMonth() != ld.lengthOfMonth()) {
				log.info("等级结算为月结算，今天不是月末，不用处理");
				return;
			}
			break;
		}
		Level2 level2 = config.getLevel2();
		Level3 level3 = config.getLevel3();
		Level4 level4 = config.getLevel4();
		Level5 level5 = config.getLevel5();
		Date now = new Date();
		Date lastSettlementDate = new Date(now.getTime() - 1000 * 3600 * 24);
		List<LevelDef> levelList = levelDefService.list();
		UserLevelChangeRecord lastRecord = userLevelChangeRecordService.lambdaQuery().orderByDesc(UserLevelChangeRecord::getCreateTime).last("limit 1").one();
		if(lastRecord != null) {
			lastSettlementDate = lastRecord.getCreateTime();
		}
		List<UserInfo> userList = userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 1).list();
		if (userList.size() > 0) {
			for (UserInfo u : userList) {
				if (hasReachedLv2(level2, u, lastSettlementDate, now)) {
					updateUserLevel(u.getId(), 1, 1, 2, levelList);
				}
			}
		}
		userList = userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 2).list();
		for (UserInfo u : userList) {
			if (!hasReachedLv2(level2, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), -1, 2, 1, levelList);
				continue;
			}
			if (hasReachedLv3(level3, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), 1, 2, 3, levelList);
			}
		}
		userList = userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 3).list();
		for (UserInfo u : userList) {
			if (!hasReachedLv3(level3, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), -1, 3, 2, levelList);
				continue;
			}
			if (hasReachedLv4(level4, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), 1, 3, 4, levelList);
			}
		}
		userList = userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 4).list();
		for (UserInfo u : userList) {
			if (!hasReachedLv4(level4, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), -1, 4, 3, levelList);
				continue;
			}
			if (hasReachedLv5(level5, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), 1, 4, 5, levelList);
			}
		}
		userList = userInfoService.lambdaQuery().eq(UserInfo::getLevelId, 5).list();
		for (UserInfo u : userList) {
			if (!hasReachedLv5(level5, u, lastSettlementDate, now)) {
				updateUserLevel(u.getId(), -1, 5, 4, levelList);
			}
		}
		log.info("============结束处理等级结算==============");
	}
	
	private boolean hasReachedLv2(Level2 level2, UserInfo u, Date startTime, Date endTime) {
		BigDecimal ZERO = BigDecimal.ZERO;
		if(level2.getMemberCountOfL1() == 0 && level2.getPersMonthlyPerformance() == ZERO && level2.getTeamMonthlyPerformance() == ZERO) {
			log.info("未配置L2升/降级参数，不处理");
			return false;
		} 
		if(level2.getMemberCountOfL1() > 0) {
			int lv1MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 1).count();
			if(lv1MemberCount < level2.getMemberCountOfL1()) {
				return false;
			}
		}
		if(level2.getPersMonthlyPerformance().compareTo(ZERO) == 1) {
			BigDecimal persPerformance = userInfoMapper.getPersPerformance(u.getId(), startTime, endTime);
			if(persPerformance.compareTo(level2.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		if(level2.getTeamMonthlyPerformance().compareTo(ZERO) == 1) {
			QueryWrapper<UserRebateDetail> qw = new QueryWrapper<>();
			qw.select("ifnull(sum(performance), 0) as performance");
			qw.eq("user_id", u.getId());
			qw.between("add_time", startTime, endTime);
			qw.eq("is_settlemented", true);
			Map<String, Object> map = userRebateDetailService.getMap(qw);
			BigDecimal teamPerformance = (BigDecimal) map.get("performance");
			if(teamPerformance.compareTo(level2.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasReachedLv3(Level3 level3, UserInfo u, Date startTime, Date endTime) {
		BigDecimal ZERO = BigDecimal.ZERO;
		if(level3.getMemberCountOfL1() == 0 && level3.getMemberCountOfL2() == 0 && level3.getPersMonthlyPerformance() == ZERO && level3.getTeamMonthlyPerformance() == ZERO && level3.getAgentCountOfL2() == 0) {
			log.info("未配置L3升/降级参数，不处理");
			return false;
		} 
		if(level3.getMemberCountOfL1() > 0) {
			int lv1MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 1).count();
			if(lv1MemberCount < level3.getMemberCountOfL1()) {
				return false;
			}
		}
		if(level3.getMemberCountOfL2() > 0) {
			int lv2MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 2).count();
			if(lv2MemberCount < level3.getMemberCountOfL2()) {
				return false;
			}
		}
		if(level3.getPersMonthlyPerformance().compareTo(ZERO) == 1) {
			BigDecimal persPerformance = userInfoMapper.getPersPerformance(u.getId(), startTime, endTime);
			if(persPerformance.compareTo(level3.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		if(level3.getTeamMonthlyPerformance().compareTo(ZERO) == 1) {
			QueryWrapper<UserRebateDetail> qw = new QueryWrapper<>();
			qw.select("ifnull(sum(performance), 0) as performance");
			qw.eq("user_id", u.getId());
			qw.between("add_time", startTime, endTime);
			qw.eq("is_settlemented", true);
			Map<String, Object> map = userRebateDetailService.getMap(qw);
			BigDecimal teamPerformance = (BigDecimal) map.get("performance");
			if(teamPerformance.compareTo(level3.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		if(level3.getAgentCountOfL2() > 0) {
			List<AgentMemberInfo> amiList = agentMemberInfoService.lambdaQuery().eq(AgentMemberInfo::getAgentId, u.getId()).list();
			if(amiList.size() == 0) {
				return false;
			}
			List<Integer> memberIds = new ArrayList<>();
			amiList.forEach(i->{
				memberIds.add(i.getMemberUserId());
			});
			int lv2MemberCount = userInfoService.lambdaQuery().in(UserInfo::getId,memberIds).eq(UserInfo::getLevelId, 2).count();
			if(lv2MemberCount < level3.getAgentCountOfL2()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasReachedLv4(Level4 level4, UserInfo u, Date startTime, Date endTime) {
		BigDecimal ZERO = BigDecimal.ZERO;
		if(level4.getMemberCountOfL1() == 0 && level4.getMemberCountOfL3() == 0 
				&& level4.getPersMonthlyPerformance() == ZERO && level4.getTeamMonthlyPerformance() == ZERO 
				&& level4.getAgentCountOfL2() == 0 && level4.getAgentCountOfL3() == 0 && level4.getTeamMemberCount() == 0) {
			log.info("未配置L4升/降级参数，不处理");
			return false;
		} 
		if(level4.getMemberCountOfL1() > 0) {
			int lv1MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 1).count();
			if(lv1MemberCount < level4.getMemberCountOfL1()) {
				return false;
			}
		}
		if(level4.getMemberCountOfL3() > 0) {
			int lv3MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 3).count();
			if(lv3MemberCount < level4.getMemberCountOfL3()) {
				return false;
			}
		}
		if(level4.getPersMonthlyPerformance().compareTo(ZERO) == 1) {
			BigDecimal persPerformance = userInfoMapper.getPersPerformance(u.getId(), startTime, endTime);
			if(persPerformance.compareTo(level4.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		if(level4.getTeamMonthlyPerformance().compareTo(ZERO) == 1) {
			QueryWrapper<UserRebateDetail> qw = new QueryWrapper<>();
			qw.select("ifnull(sum(performance), 0) as performance");
			qw.eq("user_id", u.getId());
			qw.between("add_time", startTime, endTime);
			qw.eq("is_settlemented", true);
			Map<String, Object> map = userRebateDetailService.getMap(qw);
			BigDecimal teamPerformance = (BigDecimal) map.get("performance");
			if(teamPerformance.compareTo(level4.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		List<AgentMemberInfo> amiList = agentMemberInfoService.lambdaQuery().eq(AgentMemberInfo::getAgentId, u.getId()).list();
		if(amiList.size() < level4.getTeamMemberCount()) {
			return false;
		}
		
		List<Integer> memberIds = new ArrayList<>();
		amiList.forEach(i->{
			memberIds.add(i.getMemberUserId());
		});
		if(level4.getAgentCountOfL2() > 0) {
			int lv2MemberCount = userInfoService.lambdaQuery().in(UserInfo::getId,memberIds).eq(UserInfo::getLevelId, 2).count();
			if(lv2MemberCount < level4.getAgentCountOfL2()) {
				return false;
			}
		}
		if(level4.getAgentCountOfL3() > 0) {
			int lv3MemberCount = userInfoService.lambdaQuery().in(UserInfo::getId,memberIds).eq(UserInfo::getLevelId, 3).count();
			if(lv3MemberCount < level4.getAgentCountOfL3()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasReachedLv5(Level5 level5, UserInfo u, Date startTime, Date endTime) {
		BigDecimal ZERO = BigDecimal.ZERO;
		if(level5.getMemberCountOfL1() == 0 && level5.getMemberCountOfL4() == 0 
				&& level5.getPersMonthlyPerformance() == ZERO && level5.getTeamMonthlyPerformance() == ZERO 
				&& level5.getAgentCountOfL2() == 0 && level5.getAgentCountOfL3() == 0 && level5.getAgentCountOfL4() ==0 && level5.getTeamMemberCount() == 0) {
			log.info("未配置L4升/降级参数，不处理");
			return false;
		} 
		if(level5.getMemberCountOfL1() > 0) {
			int lv1MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 1).count();
			if(lv1MemberCount < level5.getMemberCountOfL1()) {
				return false;
			}
		}
		if(level5.getMemberCountOfL4() > 0) {
			int lv4MemberCount = userInfoService.lambdaQuery().eq(UserInfo::getRegInvitationCode, u.getPromotionCode()).eq(UserInfo::getLevelId, 4).count();
			if(lv4MemberCount < level5.getMemberCountOfL4()) {
				return false;
			}
		}
		if(level5.getPersMonthlyPerformance().compareTo(ZERO) == 1) {
			BigDecimal persPerformance = userInfoMapper.getPersPerformance(u.getId(), startTime, endTime);
			if(persPerformance.compareTo(level5.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		if(level5.getTeamMonthlyPerformance().compareTo(ZERO) == 1) {
			QueryWrapper<UserRebateDetail> qw = new QueryWrapper<>();
			qw.select("ifnull(sum(performance), 0) as performance");
			qw.eq("user_id", u.getId());
			qw.between("add_time", startTime, endTime);
			qw.eq("is_settlemented", true);
			Map<String, Object> map = userRebateDetailService.getMap(qw);
			BigDecimal teamPerformance = (BigDecimal) map.get("performance");
			if(teamPerformance.compareTo(level5.getPersMonthlyPerformance()) == -1) {
				return false;
			}
		}
		List<AgentMemberInfo> amiList = agentMemberInfoService.lambdaQuery().eq(AgentMemberInfo::getAgentId, u.getId()).list();
		if(amiList.size() < level5.getTeamMemberCount()) {
			return false;
		}
		List<Integer> memberIds = new ArrayList<>();
		amiList.forEach(i->{
			memberIds.add(i.getMemberUserId());
		});
		if(level5.getAgentCountOfL2() > 0) {
			int lv2MemberCount = userInfoService.lambdaQuery().in(UserInfo::getId,memberIds).eq(UserInfo::getLevelId, 2).count();
			if(lv2MemberCount < level5.getAgentCountOfL2()) {
				return false;
			}
		}
		if(level5.getAgentCountOfL3() > 0) {
			int lv3MemberCount = userInfoService.lambdaQuery().in(UserInfo::getId,memberIds).eq(UserInfo::getLevelId, 3).count();
			if(lv3MemberCount < level5.getAgentCountOfL3()) {
				return false;
			}
		}
		if(level5.getAgentCountOfL4() > 0) {
			int lv4MemberCount = userInfoService.lambdaQuery().in(UserInfo::getId,memberIds).eq(UserInfo::getLevelId, 4).count();
			if(lv4MemberCount < level5.getAgentCountOfL4()) {
				return false;
			}
		}
		return true;
	}
	
	private void updateUserLevel(Integer userId, int changeType, int beLevelVal, int afLevelVal, List<LevelDef> levelList) {
		userInfoService.lambdaUpdate().eq(UserInfo::getId, userId).set(UserInfo::getLevelVal, afLevelVal);
		UserLevelChangeRecord r = new UserLevelChangeRecord();
		r.setUserId(userId);
		r.setChangeType(changeType);
		r.setBeLevelVal(beLevelVal);
		r.setBeLevelName(levelList.get(1).getLevelName());
		r.setAfLevelVal(afLevelVal);
		r.setAfLevelName(levelList.get(0).getLevelName());
		r.insert();
	}
}
