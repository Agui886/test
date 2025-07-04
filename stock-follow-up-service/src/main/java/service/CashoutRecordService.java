package service;

import entity.CashoutRecord;
import entity.common.Response;
import vo.manager.CashInOutStatisticsVO.CashInOutStatistics;
import vo.manager.CashoutRecordListParamVO;
import vo.manager.CashoutRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashoutRecordVO;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 提现记录表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface CashoutRecordService extends IService<CashoutRecord> {

	/**
	 * 管理系统-资金管理-提现列表
	 * @param param
	 * @return
	 */
	Response<CashoutRecordListVO> managerList(CashoutRecordListParamVO param);

	/**
	 * 资产-出金-记录
	 * @param loginId
	 * @param amount
	 * @param fundPwd
	 * @param ip
	 * @return
	 */
	Response<Void> submit(Integer loginId, BigDecimal amount, String fundPwd, String ip);

	/**
	 * 资产-出金-记录
	 * @param page
	 * @param param
	 */
	void record(Page<CashoutRecordVO> page, CashinAndOutRecordParamVO param);

	/**
	 * 充提/提现统计-提现统计
	 * @param startTime
	 * @param endTime
	 * @param orderStatus
	 * @return
	 */
	CashInOutStatistics getCashInOutStatistics(Date startTime, Date endTime, Integer orderStatus);

}
