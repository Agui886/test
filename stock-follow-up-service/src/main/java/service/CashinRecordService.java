package service;

import java.util.Date;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.CashinRecord;
import entity.common.Response;
import vo.manager.CashInOutStatisticsVO.CashInOutStatistics;
import vo.manager.CashinRecordListParamVO;
import vo.manager.CashinRecordListVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashinRecordVO;

/**
 * <p>
 * 会员充值记录表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface CashinRecordService extends IService<CashinRecord> {

	/**
	 * 管理系统-资金管理-充值列表
	 * @param param
	 * @return
	 */
	Response<CashinRecordListVO> managerList(CashinRecordListParamVO param);

	/**
	 * 资产-入金-记录
	 * @param page
	 * @param param
	 */
	void record(Page<CashinRecordVO> page, CashinAndOutRecordParamVO param);

	/**
	 * 充提/提现统计-充值统计
	 * @param startTime
	 * @param endTime
	 * @param orderStatus
	 * @return
	 */
	CashInOutStatistics getCashInOutStatistics(Date startTime, Date endTime, Integer orderStatus);
	

}
