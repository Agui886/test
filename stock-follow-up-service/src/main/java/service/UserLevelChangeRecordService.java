package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserLevelChangeRecord;
import vo.manager.UserLevelChangeRecordParamVO;

/**
 * <p>
 * 用户等级升降记录 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-24
 */
public interface UserLevelChangeRecordService extends IService<UserLevelChangeRecord> {

	/**
	 * 代理升降级记录
	 * @param page
	 * @param param
	 */
	void managerList(Page<UserLevelChangeRecord> page, UserLevelChangeRecordParamVO param);

}
