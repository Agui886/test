package service;

import entity.UserFavoriteStock;
import entity.common.Response;
import vo.server.AddFavoriteStockParamVO;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户自选股票信息 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
public interface UserFavoriteStockService extends IService<UserFavoriteStock> {

	/**
	 * 自选-股票列表
	 * @param page
	 * @param loginId
	 */
	void favoriteList(Page<UserFavoriteStock> page, Integer loginId);

	/**
	 * 自选-添加自选，可批量
	 * @param loginId
	 * @param param
	 * @return
	 */
	Response<Void> addStocks(Integer loginId, List<AddFavoriteStockParamVO> param);

}
