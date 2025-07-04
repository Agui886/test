package service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserFollowUpRecord;
import vo.manager.FollowRecordParamVO;
import vo.manager.FollowStatisticsVO.FollowStatistics;
import vo.server.UserFollowRecordVO;

/**
 * <p>
 * 用户跟投记录表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
public interface UserFollowUpRecordService extends IService<UserFollowUpRecord> {

	/**
	 * 跟投-导师信息-用户跟投记录
	 * @param loginId
	 * @param tutorId
	 * @return
	 */
	List<UserFollowRecordVO> userFollowRecord(UserFollowUpRecord param);

	/**
	 * 跟投管理-用户跟投记录
	 * @param page
	 * @param param
	 */
	void managerList(Page<UserFollowUpRecord> page, FollowRecordParamVO param);

	/**
	 * 根据跟投id，计算总市值
	 * @param positionId
	 * @return
	 */
	BigDecimal getPositionFollowSum(Integer followRecordId);

	/**
	 * 跟投报表-跟投统计
	 * @param itemType
	 * @return
	 */
	FollowStatistics getFollowStatistics(Integer itemType);

}
