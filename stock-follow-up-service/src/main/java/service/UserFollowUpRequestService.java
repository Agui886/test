package service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserFollowUpRequest;
import entity.common.Response;
import vo.manager.UserFollowUpRequestListSearchParamVO;
import vo.server.ApplyFollowRequestVO;
import vo.server.UserFollowRequestOrRecordParamVO;
import vo.server.UserFollowRequestOrRecordVO;


/**
 * <p>
 * 用户跟投申请表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
public interface UserFollowUpRequestService extends IService<UserFollowUpRequest> {

	/**
	 * 跟投管理-用户跟投审核
	 * @param page
	 * @param param
	 */
	void managerList(Page<UserFollowUpRequest> page, UserFollowUpRequestListSearchParamVO param);

	/**
	 * 跟投-用户跟投记录（审核跟投记录/跟投记录）
	 * @param page
	 * @param param
	 * @return
	 */
	Response<Page<UserFollowRequestOrRecordVO>> userFollowRequestOrRecord(Page<UserFollowRequestOrRecordVO> page, UserFollowRequestOrRecordParamVO param);

	/**
	 * 跟投-导师信息-跟单详情-申请跟投
	 * @param param
	 * @return
	 */
	Response<Void> applyFollowRequest(ApplyFollowRequestVO param);

	/**
	 * 跟投管理-用户跟投审核-状态修改
	 * @param id
	 * @param status
	 * @param operator
	 * @return
	 */
	Response<Void> updateStatus(Integer id, Integer status,String ip, String operator);

}
