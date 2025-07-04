package mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserFollowUpRecord;
import vo.manager.FollowRecordParamVO;

/**
 * <p>
 * 用户跟投记录表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
public interface UserFollowUpRecordMapper extends BaseMapper<UserFollowUpRecord> {

	/**
	 * 跟投管理-用户跟投记录
	 * @param page
	 * @param queryWrapper
	 */
	Page<UserFollowUpRecord> managerList(Page<UserFollowUpRecord> page, FollowRecordParamVO param);

}
