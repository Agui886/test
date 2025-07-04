package mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserLevelChangeRecord;
import vo.manager.UserLevelChangeRecordParamVO;

/**
 * <p>
 * 用户等级升降记录 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-24
 */
public interface UserLevelChangeRecordMapper extends BaseMapper<UserLevelChangeRecord> {

	/**
	 * 代理升降级记录
	 * @param page
	 * @param param
	 * @return
	 */
	Page<UserLevelChangeRecord> managerList(Page<UserLevelChangeRecord> page, UserLevelChangeRecordParamVO param);

}
