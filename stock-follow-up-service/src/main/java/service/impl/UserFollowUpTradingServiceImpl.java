package service.impl;

import entity.UserFollowUpTrading;
import mapper.UserFollowUpTradingMapper;
import service.UserFollowUpTradingService;
import vo.manager.TradingListSearchParamVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户-跟投-交易记录 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
@Service
public class UserFollowUpTradingServiceImpl extends ServiceImpl<UserFollowUpTradingMapper, UserFollowUpTrading> implements UserFollowUpTradingService {

	/**
	 * 跟投管理-用户交易记录
	 */
	@Override
	public void managerList(Page<UserFollowUpTrading> page, TradingListSearchParamVO param) {
		this.baseMapper.managerList(page,param);
	}

}
