package service;

import java.math.BigDecimal;

import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponseBody.ResultObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.UserInfo;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import vo.common.TokenUserVO;
import vo.manager.AddUserParamVO;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.EditUserParamVO;
import vo.manager.TeamDetailSearchVO;
import vo.manager.TeamRebateOrInviteSearchVO;
import vo.manager.UserAmtDetailVO;
import vo.manager.UserFollowStatisticsSearchParamVO;
import vo.manager.UserFollowStatisticsVO;
import vo.manager.UserListSearchParamVO;
import vo.server.AliyunInitFaceVerifyParamVO;
import vo.server.AssetsDetailVO;
import vo.server.RegisterParamVO;

/**
 * <p>
 * 会员信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface UserInfoService extends IService<UserInfo> {

	/**
	 * 管理系统->用户管理->用户列表->查询
	 * @param page
	 * @param vo
	 * @return
	 */
	void managerUserList(Page<UserInfo> page, UserListSearchParamVO vo);

	/**
	 * 管理系统->用户管理->用户列表->添加用户
	 * @param vo
	 * @return
	 */
	Response<Void> managerUserAdd(AddUserParamVO vo, String ip, String operator);

	/**
	 * 管理系统->用户管理->用户列表->编辑用户
	 * @param vo
	 * @return
	 */
	Response<Void> managerUserEdit(EditUserParamVO vo, String ip, String operator);

	/**
	 * 强制下线
	 * @param userId
	 * @return
	 */
	Response<Void> forcedOffline(Integer userId);

	/**
	 * 用户资金流水变更通用方法
	 * @param userId 用户id
	 * @param deType 变更类型
	 * @param amt 变更金额
	 * @param deSummary 变更详细说明
	 * @param currency 币种
	 * @param exchangeRate 汇率
	 * @param ip ip
	 * @param ipAddress ip地址
	 * @param operator 操作人
	 * @param businessOrderSn 业务单号
	 */
	void updateUserAvailableAmt(Integer userId,  AmtDeTypeEnum deType, BigDecimal amt, String deSummary, CurrencyEnum currency, BigDecimal exchangeRate, String ip, String ipAddress, String operator,String businessOrderSn) ;

	/**
	 * 获取用户资金信息
	 * @param userId
	 * @return
	 */
	UserAmtDetailVO getUserAmtDetail(Integer userId);

	/**
	 * 用户注册验证
	 * @param param
	 * @return
	 */
	Response<Void> registerVerify(RegisterParamVO param);

	/**
	 * 用户注册
	 * @param param
	 * @param ip
	 * @return
	 */
	Response<Void> register(RegisterParamVO param, String ip);

	/**
	 * 会员登录
	 * @param areaCode
	 * @param loginAccount
	 * @param loginPwd
	 * @param loginType
	 * @param ip
	 * @param requestUrl
	 * @return
	 */
	Response<TokenUserVO> login(String areaCode, String loginAccount, String loginPwd, int loginType, String ip,
			String requestUrl);

	/**
	 * 会员登出
	 * @param userId
	 * @param ip
	 * @param requestUrl
	 * @return
	 */
	Response<Void> logout(Integer userId, String ip, String requestUrl);

	/**
	 * 阿里云-发起人脸认证
	 * @param param
	 * @return
	 */
	Response<ResultObject> doAliyunInitFaceVerify(AliyunInitFaceVerifyParamVO param);
	
	/**
	 * 阿里云-获取人脸认证结果
	 * @param certifyId
	 * @return
	 */
	Response<String> doAliyunDescribeFaceVerify(String certifyId);

	/**
	 * 执行-人脸认证业务
	 * @param certifyId
	 * @param passed
	 * @return
	 */
	Response<String> doFaceVerifyBusiness(String certifyId, String passed);

	/**
	 * 忘记密码(人脸认证-修改密码)
	 * @param certifyId
	 * @return
	 */
	Response<Void> doFaceVerifyModifyLoginPwd(String certifyId, String newLoginPwd, String ip);

	/**
	 * 设置-安全设置-修改手机号码
	 * @param loginId
	 * @param areaCode
	 * @param phone
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> modifyPhone(Integer userId, String areaCode, String phone, String ip, String operator);

	/**
	 * 设置-安全设置-修改邮箱
	 * @param loginId
	 * @param areaCode
	 * @param phone
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> modifyEmail(Integer userId, String email, String ip, String operator);

	/**
	 * 设置-安全设置-修改登录密码
	 * @param loginId
	 * @param oldLoginPwd
	 * @param newLoginPwd
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> modifyLoginPwd(Integer userId, String oldLoginPwd, String newLoginPwd, String ip, String operator);

	/**
	 * 设置-安全设置-修改交易密码
	 * @param loginId
	 * @param oldPwd
	 * @param newFundPwd
	 * @param ip
	 * @param operator
	 * @return
	 */
	Response<Void> modifyFundPwd(Integer userId, String oldPwd, String newFundPwd, String ip, String operator);

	/**
	 * 设置-安全设置-密码验证
	 * @param loginId
	 * @param pwd
	 * @param pwdType
	 * @return
	 */
	Response<Void> pwdVerify(Integer userId, String pwd, int pwdType);

	void managerAgentList(Page<AgentListVO> page, AgentListSearchParamVO param);
	/**
	 * 资产明细
	 * @param loginId
	 * @return
	 */
	Response<AssetsDetailVO> assetsDetail(Integer loginId);

	Response<TeamDetailSearchVO> teamDetail(Integer userId);
	
	Response<TeamRebateOrInviteSearchVO> teamRebates(Integer userId);
	
	Response<TeamRebateOrInviteSearchVO> invitedMembers(Integer userId);
	
	/**
	 * 跟据交易添加返佣明细
	 * @param fromUser 来源用户
	 * @param performance 业绩金额，这里固定为cny币种，调用时需提前转换汇率
	 * @param type 0-跟投的买卖交易，1-用户个人买入股票买，2-用户个人卖了股票
	 */
	void addUserRebateDetailOnTransaction(UserInfo fromUser, BigDecimal performance, Integer type, String tradingOrderSn);

	/**
	 * 数据报表-用户跟投统计
	 * @param page
	 * @param param
	 */
	void userFollowStatistics(Page<UserFollowStatisticsVO> page, UserFollowStatisticsSearchParamVO param);
}
