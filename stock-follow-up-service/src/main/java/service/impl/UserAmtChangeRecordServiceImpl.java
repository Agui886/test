package service.impl;

import entity.UserAmtChangeRecord;
import mapper.UserAmtChangeRecordMapper;
import service.UserAmtChangeRecordService;
import vo.manager.UserAmtChangeRecordSearchParamVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员资金变更记录表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Service
public class UserAmtChangeRecordServiceImpl extends ServiceImpl<UserAmtChangeRecordMapper, UserAmtChangeRecord> implements UserAmtChangeRecordService {

	@Resource
	private UserAmtChangeRecordMapper userAmtChangeRecordMapper;
	
	@Override
	public void managerList(Page<UserAmtChangeRecord> page, UserAmtChangeRecordSearchParamVO param) {
		userAmtChangeRecordMapper.managerList(page, param);
	}
}
