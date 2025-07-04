package mapper;

import entity.SiteHotStock;
import vo.server.SearchStockResultVO;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 站点热门股票 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface SiteHotStockMapper extends BaseMapper<SiteHotStock> {

	/**
	 * 热门搜索-股票查询结果列表
	 * @param page
	 * @param keywords
	 * @param userId
	 */
	@Select("SELECT  "
			+ "    stock_type, "
			+ "    stock_name, "
			+ "    stock_code, "
			+ "    case when #{userId}=0 then 0 else "
			+ "    (SELECT COUNT(1) FROM user_favorite_stock f WHERE f.stock_code = s.stock_code AND f.stock_type = s.stock_type AND f.user_id = #{userId}) end AS isFavorite "
			+ "FROM "
			+ "    stock_info s where s.stock_code like '%${keywords}%' or s.stock_name like '%${keywords}%' "
			+"ORDER BY CHAR_LENGTH(s.stock_code)")
	Page<SearchStockResultVO> stockResultByKeywords(Page<SearchStockResultVO> page, @Param("keywords") String keywords, @Param("userId") Integer userId);

}
