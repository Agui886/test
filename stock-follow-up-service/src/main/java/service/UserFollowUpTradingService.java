package service;

import entity.UserFollowUpTrading;
import vo.manager.TradingListSearchParamVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户-跟投-交易记录 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
public interface UserFollowUpTradingService extends IService<UserFollowUpTrading> {

	/**
	 * 跟投管理-用户交易记录
	 * @param page
	 * @param param
	 */
	void managerList(Page<UserFollowUpTrading> page, TradingListSearchParamVO param);

}
