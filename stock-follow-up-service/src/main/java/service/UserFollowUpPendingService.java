package service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserFollowUpPending;
import entity.common.Response;
import io.swagger.annotations.ApiParam;
import vo.manager.FollowPendingListSearchParamVO;
import vo.manager.FollowPendingTransferDetailUserVO;
import vo.manager.FollowPendingTransferDetailVO;
import vo.manager.FollowPendingTransferFinishParamVO;
import vo.server.UserFollowPendingParamVO;
import vo.server.UserFollowPendingVO;

/**
 * <p>
 * 用户跟投委托表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-13
 */
public interface UserFollowUpPendingService extends IService<UserFollowUpPending> {

	/**
	 * 跟投-导师信息-跟单详情-委托单记录
	 * @param page
	 * @param param
	 */
	void userFollowPending(Page<UserFollowPendingVO> page, UserFollowPendingParamVO param);

	/**
	 * 跟投管理-用户跟投委托
	 * @param page
	 * @param param
	 */
	void managerList(Page<UserFollowUpPending> page, FollowPendingListSearchParamVO param);

	/**
	 * 委托订单-待转完成-详情
	 * @param pendingIdList 委托单ID,List
	 * @param signId 信号id
	 * @return
	 */
	Response<FollowPendingTransferDetailVO> transferDetail(List<Integer> pendingIdList, Integer signId,BigDecimal buyingPrice,Boolean marketPrice);

	/**
	 * 委托订单-撤单操作
	 * @param id
	 * @return
	 */
	Response<Void> revoke(List<Integer> pendingIdList,String ip,String operator);

	/**
	 * 委托订单-完成操作
	 * @param param 委托订单-完成操作，请求参数
	 * @param ip ip
	 * @param operator 操作人
	 * @return
	 */
	Response<Void> transferFinish(FollowPendingTransferFinishParamVO param, String ip, String operator);




}
