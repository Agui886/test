package service;

import entity.UserAmtChangeRecord;
import vo.manager.UserAmtChangeRecordSearchParamVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员资金变更记录表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface UserAmtChangeRecordService extends IService<UserAmtChangeRecord> {

	void managerList(Page<UserAmtChangeRecord> page, UserAmtChangeRecordSearchParamVO param);

}
