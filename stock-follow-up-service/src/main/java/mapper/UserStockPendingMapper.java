package mapper;

import entity.UserStockPending;
import vo.manager.PendingListSearchParamVO;
import vo.manager.PendingListVO;
import vo.server.StockPendingListVO;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户股票委托订单表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserStockPendingMapper extends BaseMapper<UserStockPending> {

	/**
	 * 持仓管理-委托订单
	 * @param page
	 * @param param
	 * @return
	 */
	Page<PendingListVO> managerPendingList(Page<PendingListVO> page, @Param("param") PendingListSearchParamVO param);
	
	/**
	 * APP资产-挂单列表
	 * @param page
	 * @param userId
	 * @param positionStatus
	 * @return
	 */
	@Select("select id,stock_name,stock_code,stock_type,stock_plate,buying_shares,position_direction,lever,buying_price,position_status,is_block_trading,pending_time from user_stock_pending where user_id=#{userId} and position_status=#{positionStatus} ORDER BY pending_time DESC")
	Page<StockPendingListVO> pendingList(Page<StockPendingListVO> page, @Param("userId") Integer userId, @Param("positionStatus") Integer positionStatus);

	/**
	 * 冻结股数
	 * @param positionId
	 * @param lockInPeriod
	 * @return
	 */
	@Select("SELECT ifnull(sum(buying_shares), 0) FROM user_stock_pending p where p.position_id=#{positionId} and p.position_time>date_sub(sysdate(), interval #{lockInPeriod} day)")
	int getUnavailableShares(@Param("positionId") Integer positionId, @Param("lockInPeriod") Integer lockInPeriod);

}
