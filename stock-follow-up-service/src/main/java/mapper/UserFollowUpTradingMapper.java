package mapper;

import entity.UserFollowUpTrading;
import vo.manager.TradingListSearchParamVO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户-跟投-交易记录 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-28
 */
public interface UserFollowUpTradingMapper extends BaseMapper<UserFollowUpTrading> {

	/**
	 * 跟投管理-用户交易记录
	 * @param page
	 * @param param
	 */
	Page<UserFollowUpTrading> managerList(Page<UserFollowUpTrading> page,@Param("param") TradingListSearchParamVO param);

	/**
	 * 通过持仓id，查买入的交易记录
	 * @param positionId
	 * @return
	 */
	@Select("SELECT"
			+ "  followTrading.* "
			+ "FROM"
			+ "  user_follow_up_trading followTrading"
			+ "  LEFT JOIN sign_info signInfo ON followTrading.sign_id = signInfo.id "
			+ "WHERE"
			+ "  signInfo.sign_type = #{signType} "
			+ "  AND followTrading.position_id = #{positionId} "
			+ "ORDER BY"
			+ "  followTrading.trading_time")
	List<UserFollowUpTrading> userFollowUpTradingPositionList(@Param("positionId") Integer positionId,@Param("signType") Integer signType);

}
