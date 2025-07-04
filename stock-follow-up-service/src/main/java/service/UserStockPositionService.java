package service;

import entity.UserStockPosition;
import entity.common.Response;
import vo.manager.StockPositionListSearchParamVO;
import vo.manager.StockPositionListVO;
import vo.server.StockPositionDetailVO;
import vo.server.UserStockPositionListVO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户持仓信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-14
 */
public interface UserStockPositionService extends IService<UserStockPosition> {

	/**
	 * 持仓管理-股票持仓
	 * @param page
	 * @param param
	 * @param b
	 */
	void managerStockPositionList(Page<StockPositionListVO> page, StockPositionListSearchParamVO param, boolean b);

	/**
	 * 持仓管理-股票持仓-锁仓
	 * @param id
	 * @param lockMsg
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> lock(Integer id, String lockMsg, String ip, String operator);
	
	/**
	 * 持仓管理-股票持仓-解锁
	 * @param id
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> unlock(Integer id, String ip, String operator);
	
	/**
	 * 持仓管理-股票持仓-修改锁仓天数
	 * @param id
	 * @param lockInPeriod
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> lockInPeriodEdit(Integer id, Integer lockInPeriod, String ip, String operator);
	
	/**
	 * 持仓管理-股票持仓-强制平仓
	 * @param id
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> managerClosePosition(Integer id, String ip, String operator);
	
	/**
	 * APP股票-卖出-加载持仓列表
	 * @param page
	 * @param loginId
	 * @param stockType
	 * @param stockCode
	 */
	void stockPositionList(Page<UserStockPositionListVO> page, Integer loginId, String stockType, String stockCode);

	/**
	 * APP资产-持仓详情
	 * @param id
	 * @param loginId
	 * @return
	 */
	Response<StockPositionDetailVO> stockPositionDetail(Integer id, Integer loginId);

	

	

	


	
	
	
}
