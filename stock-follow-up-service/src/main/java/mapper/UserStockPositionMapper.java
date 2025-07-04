package mapper;

import entity.UserStockPosition;
import vo.manager.StockPositionListSearchParamVO;
import vo.manager.StockPositionListVO;
import vo.server.UserStockPositionListVO;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户持仓信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserStockPositionMapper extends BaseMapper<UserStockPosition> {

Page<StockPositionListVO> managerStockPositionList(Page<StockPositionListVO> page, @Param("param") StockPositionListSearchParamVO param, @Param("isBlockTrading") boolean isBlockTrading);
	
	@Select("SELECT  p.id,"
			+ "    stock_code, "
			+ "    stock_name, "
			+ "    stock_type, "
			+ "    stock_plate, "
			+ "    user_id, "
			+ "    a.id AS agentId, "
			+ "    a.agent_name, "
			+ "    buying_shares, "
			+ "    position_direction, "
			+ "    position_time "
			+ "FROM "
			+ "    user_stock_position p "
			+ "        INNER JOIN "
			+ "    user_info u ON p.user_id = u.id "
			+ "        left JOIN "
			+ "    agent_info a ON u.agent_id = a.id where position_status=2 "
			+ "order by position_time desc limit 20")
	List<StockPositionListVO> newStockPositions();
	
	@Select("<script>"
			+ "SELECT  "
			+ "    id, "
			+ "    stock_name, "
			+ "    stock_code, "
			+ "    stock_type, "
			+ "    stock_plate, "
			+ "    position_direction, "
			+ "    buying_price, "
			+ "    buying_shares, "
			+ "    lever, "
			+ "    position_time, "
			+ "    is_block_trading "
			+ "FROM "
			+ "    user_stock_position "
			+ "WHERE "
			+ "    user_id = #{userId} AND position_status = 2"
			+ "<if test=\"stockType != null and stockType !=''\">"
			+ " and stock_type=#{stockType}"
			+ "</if>"
			+ "<if test=\"stockCode != null and stockCode !=''\">"
			+ " and stock_code=#{stockCode}"
			+ "</if>"
			+ " ORDER BY position_time DESC "
			+ "</script>")
	Page<UserStockPositionListVO> stockPositionList(Page<UserStockPositionListVO> page, @Param("userId") Integer userId, @Param("stockType") String stockType, @Param("stockCode") String stockCode);
}
