package service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserRebateDetail;
import entity.common.Response;
import vo.manager.RebateDetailListVO;
import vo.manager.RebateDetailParamVO;
import vo.manager.RebateRecordParamVO;
import vo.manager.RebateRecordVO;
import vo.manager.RebateStatisticsVO.RebateStatistics;
import vo.server.UserRebateDetailParamVO;
import vo.server.UserRebateDetailVO;

/**
 * <p>
 * 用户返佣记录明细 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
public interface UserRebateDetailService extends IService<UserRebateDetail> {

	/**
	 * 获取返佣统计数据
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param levelVal
	 * @return
	 */
	RebateStatistics getRebateStatistics(Integer userId, Date startTime, Date endTime, Integer levelVal);

	/**
	 * 我的-资产-代理中心-返佣明细-会员返佣详情
	 * @param page
	 * @param param
	 * @return
	 */
	Response<UserRebateDetailVO> userRebateDetail(UserRebateDetailParamVO param);

	/**
	 * 返佣用户list，返佣金额总计排序
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param levelVal
	 * @return
	 */
	List<RebateStatistics> getUserRebateStatistics(Integer userId, Date startTime, Date endTime, Integer levelVal);
	
	void managerList(Page<RebateRecordVO> page, RebateRecordParamVO param);
	
	void managerDetailList(Page<RebateDetailListVO> page, RebateDetailParamVO param);

}
