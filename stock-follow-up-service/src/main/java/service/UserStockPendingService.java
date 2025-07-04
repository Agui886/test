package service;

import entity.UserStockPending;
import entity.common.Response;
import vo.manager.PendingListSearchParamVO;
import vo.manager.PendingListVO;
import vo.manager.PendingTransferDetailVO;
import vo.manager.TransferPositionSearchParamVO;
import vo.server.BuyStockParamVO;
import vo.server.BuyingStockPageVO;
import vo.server.StockPendingDetailVO;
import vo.server.StockPendingListVO;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户股票委托订单表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserStockPendingService extends IService<UserStockPending> {

	/**
	 * 持仓管理-委托订单
	 * @param page
	 * @param param
	 */
	void managerPendingList(Page<PendingListVO> page, PendingListSearchParamVO param);
	
	/**
	 * 持仓管理-委托订单-转入详情（参数传委托单id）
	 * @param id
	 * @return
	 */
	Response<PendingTransferDetailVO> transferDetail(Integer id);
	
	/**
	 * 持仓管理-委托订单-转入持仓（单笔和批量通用，参数传委托单id集合，格式json[1,2,3,n..]）
	 * @param searchParamList
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> pendingTransferPosition(List<TransferPositionSearchParamVO> searchParamList, String ip,
			String operator);
	
	/**
	 * 持仓管理-委托订单-拒绝订单（单笔和批量通用，参数传委托单id集合，格式json[1,2,3,n..]）
	 * @param ids
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> reject(List<Integer> ids, String ip, String operator);
	
	/**
	 * APP股票-买入-页面数据加载
	 * @param loginId
	 * @param stockCode
	 * @param stockType
	 * @return
	 */
	Response<BuyingStockPageVO> buyingStockPage(Integer userId, String stockCode, String stockType);

	/**
	 * APP股票-买入
	 * @param loginId
	 * @param param
	 * @param ip
	 * @return
	 */
	Response<Void> buyStock(Integer userId, BuyStockParamVO param, String ip);

	/**
	 * APP资产-挂单列表
	 * @param page
	 * @param loginId
	 * @param positionStatus
	 */
	void pendingList(Page<StockPendingListVO> page, Integer userId, Integer positionStatus);

	/**
	 * APP资产-挂单详情
	 * @param id
	 * @param loginId
	 * @return
	 */
	Response<StockPendingDetailVO> pendingDetail(Integer id, Integer userId);

	/**
	 * APP资产-挂单取消、撤单
	 * @param id
	 * @param loginId
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> cancel(Integer id, Integer userId, String ip, String operator);

	

	

	

	

}
