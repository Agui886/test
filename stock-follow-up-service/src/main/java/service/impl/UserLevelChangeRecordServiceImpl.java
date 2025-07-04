package service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.UserLevelChangeRecord;
import mapper.UserLevelChangeRecordMapper;
import service.UserLevelChangeRecordService;
import vo.manager.UserLevelChangeRecordParamVO;

/**
 * <p>
 * 用户等级升降记录 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-24
 */
@Service
public class UserLevelChangeRecordServiceImpl extends ServiceImpl<UserLevelChangeRecordMapper, UserLevelChangeRecord> implements UserLevelChangeRecordService {

	/**
	 * 代理升降级记录
	 */
	@Override
	public void managerList(Page<UserLevelChangeRecord> page, UserLevelChangeRecordParamVO param) {
		this.baseMapper.managerList(page,param);
	}

}
