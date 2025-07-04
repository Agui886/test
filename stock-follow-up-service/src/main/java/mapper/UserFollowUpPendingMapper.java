package mapper;

import entity.UserFollowUpPending;
import vo.manager.FollowPendingListSearchParamVO;
import vo.server.UserFollowPendingParamVO;
import vo.server.UserFollowPendingVO;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户跟投委托表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
public interface UserFollowUpPendingMapper extends BaseMapper<UserFollowUpPending> {

	/**
	 * 跟投-导师信息-跟单详情-委托单记录
	 * @param page
	 * @param param
	 */
	Page<UserFollowPendingVO> userFollowPending(Page<UserFollowPendingVO> page, @Param("param") UserFollowPendingParamVO param);

	/**
	 * 跟投管理-用户跟投委托
	 * @param page
	 * @param param
	 */
	Page<UserFollowUpPending> managerList(Page<UserFollowUpPending> page, @Param("param") FollowPendingListSearchParamVO param);

	/**
	 * 上月-信号跟随-人数（用户去重）
	 * @param tutorId
	 * @param itemType
	 * @return
	 */
	@Select("<script>"
			+ "SELECT "
			+ "  COUNT(DISTINCT followPending.user_id) "
			+ "FROM "
			+ "  user_follow_up_pending followPending"
			+ "  LEFT JOIN sign_info signInfo ON followPending.sign_id = signInfo.id "
			+ "WHERE "
			+ "  followPending.tutor_id = #{tutorId} "
			+ "	 AND signInfo.display_release_time >= DATE_FORMAT(CURRENT_DATE - INTERVAL 1 MONTH, '%Y-%m-01') "
			+ "  AND signInfo.display_release_time &lt; DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') "
			+ "<if test=\"itemType != null\">"
			+ "  AND signInfo.item_type=#{itemType}"
			+ "</if>"
			+ "</script>")
	Integer getLastMonthSignTraceUser(@Param("tutorId") Integer tutorId, @Param("itemType") Integer itemType);

	/**
	 * 上月-信号跟随-卖出总次数
	 * @param tutorId
	 * @param itemType
	 * @return
	 */
	@Select("<script>"
			+ "SELECT "
			+ " COUNT(*) "
			+ "FROM "
			+ "  user_follow_up_pending followPending"
			+ "  LEFT JOIN sign_info signInfo ON followPending.sign_id = signInfo.id "
			+ "WHERE "
			+ "  followPending.tutor_id = #{tutorId} "
			+ "	 AND signInfo.display_release_time >= DATE_FORMAT(CURRENT_DATE - INTERVAL 1 MONTH, '%Y-%m-01') "
			+ "  AND signInfo.sign_type = 1"
			+ "  AND signInfo.display_release_time &lt; DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') "
			+ "<if test=\"itemType != null\">"
			+ "  AND signInfo.item_type=#{itemType}"
			+ "</if>"
			+ "</script>")
	Integer getLastMonthSignTraceSaleTotal(@Param("tutorId") Integer tutorId, @Param("itemType") Integer itemType);

}
