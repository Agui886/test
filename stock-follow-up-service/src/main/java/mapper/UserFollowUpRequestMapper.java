package mapper;


import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserFollowUpRequest;
import vo.manager.UserFollowUpRequestListSearchParamVO;
import vo.server.UserFollowRequestOrRecordParamVO;
import vo.server.UserFollowRequestOrRecordVO;


/**
 * <p>
 * 用户跟投申请表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface UserFollowUpRequestMapper extends BaseMapper<UserFollowUpRequest> {

	/**
	 * 跟投管理-用户跟投审核
	 * @param page
	 * @param param
	 * @return
	 */
	Page<UserFollowUpRequest> managerList(Page<UserFollowUpRequest> page, @Param("param") UserFollowUpRequestListSearchParamVO param);

	/**
	 * 跟投-用户跟投记录（审核跟投记录/跟投记录）
	 * @param page
	 * @param param
	 * @return
	 */
	Page<UserFollowRequestOrRecordVO> userFollowRequestOrRecord(Page<UserFollowRequestOrRecordVO> page, UserFollowRequestOrRecordParamVO param);

}
