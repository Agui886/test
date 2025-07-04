package mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserInfo;
import vo.common.ChildAndParentVO;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.TeamMembers;
import vo.manager.UserFollowStatisticsSearchParamVO;
import vo.manager.UserFollowStatisticsVO;
import vo.manager.UserListSearchParamVO;

/**
 * <p>
 * 会员信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

	/**
	 * 用户下级
	 * @param userId
	 * @return
	 */
	List<ChildAndParentVO> pNextUsers(@Param("userId") Integer userId);
	
	/**
	 * 用户上级
	 * @param agentId
	 * @return
	 */
	List<ChildAndParentVO> pNextAgents(@Param("agentId") Integer agentId);
	
	/**
	 * 管理系统->用户管理->用户列表->查询
	 * @param page
	 * @param vo
	 * @param ids
	 * @return
	 */
	Page<UserInfo> managerUserList(Page<UserInfo> page, @Param("param")UserListSearchParamVO vo, @Param("ids") Set<Integer> ids);
	
	/**
	 * 用户不可用金额
	 * @param userId
	 * @return
	 */
	@Select("SELECT  "
			+ "    (SELECT  "
			+ "            IFNULL(SUM(amount_received), 0) "
			+ "        FROM "
			+ "            user_stock_closing_position "
			+ "        WHERE "
			+ "            stock_type IN ('sh' , 'sz', 'bj', 'us') and user_id=#{userId}"
			+ "                AND transaction_status = 1 "
			+ "                AND transaction_time > DATE_SUB(SYSDATE(), INTERVAL 1 DAY)) + (SELECT  "
			+ "            IFNULL(SUM(amount_received), 0) "
			+ "        FROM "
			+ "            user_stock_closing_position "
			+ "        WHERE "
			+ "            stock_type = 'hk' and user_id=#{userId}"
			+ "                AND transaction_status = 1 "
			+ "                AND transaction_time > DATE_SUB(SYSDATE(), INTERVAL 2 DAY))")
	BigDecimal getUserUnavailableWithdrawalAmt(@Param("userId") Integer userId);
	
	Page<AgentListVO> managerAgentList(Page<AgentListVO> page, @Param("param") AgentListSearchParamVO param);
	
	@Select("<script>"
			+ "SELECT  "
			+ "    u.id as userId, "
			+ "    u.nickname, "
			+ "    l.level_val, "
			+ "    l.level_name, "
			+ "    u.agent_id, "
			+ "    u.reg_time, "
			+ "    member_user_id, "
			+ "	   member_agent_id,"
			+ " (select nickname from user_info i where i.id=m.member_agent_id) as memberAgentNickname,"
			+ " (select nickname from user_info i where i.id=m.member_user_id) as memberNickname,"
			+ " (select level_val from user_info i inner join level_def d on i.id=m.member_user_id and d.id=i.level_id) as memberLevelVal,"
			+ " (select level_name from user_info i inner join level_def d on i.id=m.member_user_id and d.id=i.level_id) as memberLevelName,"
			+ " (SELECT reg_time FROM user_info i WHERE i.id = m.member_user_id) AS memberRegTime"
			+ " FROM "
			+ "    user_info u "
			+ "        INNER JOIN "
			+ "    level_def l ON u.level_id = l.id "
			+ "        left JOIN "
			+ "    agent_member_info m ON u.id = m.member_user_id "
			+ "where m.agent_id=#{userId}"
			+ "<if test=\"regTimeStart != null\">"
			+ "	and u.reg_time >= #{regTimeStart} "
			+ "</if>"
			+ "<if test=\"regTimeEnd != null\">"
			+ "	and u.reg_time &lt;= #{regTimeEnd} "
			+ "</if>"
			+ "</script>")
	List<TeamMembers> teamMembers(@Param("userId") Integer userId, @Param("regTimeStart") Date regTimeStart, @Param("regTimeEnd") Date regTimeEnd);

	BigDecimal getPersPerformance(@Param("userId") Integer userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

	/**
	 * 数据报表-用户跟投统计
	 * @param page
	 * @param param
	 */
	Page<UserFollowStatisticsVO> userFollowStatistics(Page<UserFollowStatisticsVO> page, UserFollowStatisticsSearchParamVO param);
}
