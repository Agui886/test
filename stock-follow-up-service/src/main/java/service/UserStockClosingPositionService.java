package service;

import entity.UserStockClosingPosition;
import entity.common.Response;
import vo.manager.StockClosingPositionListVO;
import vo.manager.StockPositionListSearchParamVO;
import vo.server.SellingStockPageVO;
import vo.server.UserStockClosingPositionDetailVO;
import vo.server.UserStockClosingPositionListVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户股票平仓订单 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserStockClosingPositionService extends IService<UserStockClosingPosition> {

	/**
	 * 持仓管理-股票持仓-平仓单
	 * @param page
	 * @param param
	 * @param isBlockTrading
	 */
	void managerStockClosingList(Page<StockClosingPositionListVO> page, StockPositionListSearchParamVO param,
			boolean isBlockTrading);
	
	/**
	 * APP资产-平仓列表
	 * @param page
	 * @param userId
	 */
	void userStockClosingPositionList(Page<UserStockClosingPositionListVO> page, Integer userId);
	
	/**
	 * APP资产-平仓详情
	 * @param id
	 * @param userId
	 * @return
	 */
	Response<UserStockClosingPositionDetailVO> userStockClosingPositionDetail(Integer id, Integer userId);
	
	/**
	 * APP股票-卖出-页面数据加载
	 * @param id
	 * @param userId
	 * @return
	 */
	Response<SellingStockPageVO> sellingStockPage(Integer id, Integer userId);

	/**
	 * APP股票-卖出
	 * @param id
	 * @param userId
	 * @param shares
	 * @param ip
	 * @return
	 */
	Response<Void> sellStock(Integer id, Integer userId, Integer shares, String ip);

	

}
