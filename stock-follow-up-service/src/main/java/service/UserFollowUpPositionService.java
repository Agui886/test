package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserFollowUpPosition;
import vo.manager.FollowPositionListSearchParamVO;

/**
 * <p>
 * 用户-跟投-持仓信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
public interface UserFollowUpPositionService extends IService<UserFollowUpPosition> {

	/**
	 * 跟投管理-用户跟投持仓
	 * @param page
	 * @param param
	 * @param b
	 */
	void managerList(Page<UserFollowUpPosition> page, FollowPositionListSearchParamVO param);

	/**
	 * 初始化，用户持仓数据（获取用户可合并，持仓数据，有就合并，没有就创建）
	 * @param userId
	 * @param tutorId
	 * @param followRecordId
	 * @param stockName
	 * @param stockCode
	 * @param stockType
	 * @return
	 */
	UserFollowUpPosition getInitUserPositionMerge(Integer userId, Integer tutorId, Integer followRecordId,
			String stockName,String stockCode, String stockType);

	/**
	 * 跟投-跟投详情（平仓记录）
	 * @param page
	 * @param param
	 * @return
	 */
	void UserFollowPosition(Page<UserFollowUpPosition> page, FollowPositionListSearchParamVO param);

}
