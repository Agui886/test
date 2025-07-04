package service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.SiteHotStock;
import entity.common.Response;
import vo.server.SearchStockResultVO;

/**
 * <p>
 * 站点热门股票 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface SiteHotStockService extends IService<SiteHotStock> {

	/**
	 * 热门搜索-热门股票列表
	 * @return
	 */
	Response<List<SiteHotStock>> hotSearchStockList();

	/**
	 * 热门搜索-股票查询结果列表
	 * @param userId
	 * @param keywords
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	Response<Page<SearchStockResultVO>> stockResultByKeywords(Integer userId, String keywords, Integer pageNo,
			Integer pageSize);

}
