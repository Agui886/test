package mapper;

import entity.AgentMemberInfo;
import vo.common.ChildAndParentVO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 代理的下级会员信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-16
 */
public interface AgentMemberInfoMapper extends BaseMapper<AgentMemberInfo> {

	@Select("select member_user_id as childId, member_agent_id as parentId from agent_member_info where agent_id=#{agentId} order by id")
	List<ChildAndParentVO> agentMemberInfoList(@Param("agentId") Integer agentId);

}
