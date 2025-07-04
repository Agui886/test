package mapper;

import entity.UserStockClosingPosition;
import vo.manager.StockClosingPositionListVO;
import vo.manager.StockPositionListSearchParamVO;
import vo.server.UserStockClosingPositionListVO;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户股票平仓订单 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserStockClosingPositionMapper extends BaseMapper<UserStockClosingPosition> {

	/**
	 * 持仓管理-股票持仓-平仓单
	 * @param page
	 * @param param
	 * @param isBlockTrading
	 * @return
	 */
	Page<StockClosingPositionListVO> managerStockClosingList(Page<StockClosingPositionListVO> page, @Param("param") StockPositionListSearchParamVO param, @Param("isBlockTrading") boolean isBlockTrading);

	/**
	 * APP资产-平仓列表
	 * @param page
	 * @param userId
	 * @return
	 */
	@Select("SELECT  "
			+ "    id, "
			+ "    stock_code, "
			+ "    stock_name, "
			+ "    stock_type, "
			+ "    stock_plate, "
			+ "    buying_price, "
			+ "    actual_profit, "
			+ "    position_direction, "
			+ "    lever, "
			+ "    selling_price, "
			+ "    selling_shares, "
			+ "    is_block_trading "
			+ "FROM "
			+ "    user_stock_closing_position where user_id=#{userId} ORDER BY closing_time DESC")
	Page<UserStockClosingPositionListVO> userStockClosingPositionList(Page<UserStockClosingPositionListVO> page, @Param("userId") Integer userId);
}
