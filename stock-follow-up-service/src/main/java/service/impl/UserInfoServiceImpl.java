package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.sdk.service.cloudauth20190307.models.DescribeFaceVerifyResponse;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyRequest;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponse;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponseBody.ResultObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.json.JSONUtil;
import constant.Constant;
import entity.AgentMemberInfo;
import entity.Country;
import entity.IpAddress;
import entity.LevelDef;
import entity.UserAmtChangeRecord;
import entity.UserDataChangeRecord;
import entity.UserInfo;
import entity.UserLoginLog;
import entity.UserRebateDetail;
import entity.UserStockClosingPosition;
import entity.UserStockPending;
import entity.UserStockPosition;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.StockTypeEnum;
import enums.UserDataChangeTypeEnum;
import enums.UserFundingStatusEnum;
import enums.UserRealAuthStatusEnum;
import mapper.AgentMemberInfoMapper;
import mapper.UserInfoMapper;
import redis.RedisKeyPrefix;
import service.CountryService;
import service.IpAddressService;
import service.LevelDefService;
import service.SysParamConfigService;
import service.UserInfoService;
import service.UserStockClosingPositionService;
import service.UserStockPendingService;
import service.UserStockPositionService;
import utils.PasswordGenerator;
import utils.RedisDao;
import utils.SinaApi;
import utils.StringUtil;
import utils.aliyun.AliyunFaceUtil;
import utils.aliyun.DescribeFaceVerify;
import utils.aliyun.InitFaceVerify;
import vo.common.ChildAndParentVO;
import vo.common.Members;
import vo.common.OnlineUserVO;
import vo.common.StockQuotesVO;
import vo.common.TokenUserVO;
import vo.manager.AddUserParamVO;
import vo.manager.AgentListSearchParamVO;
import vo.manager.AgentListVO;
import vo.manager.EditUserParamVO;
import vo.manager.TeamDetailSearchVO;
import vo.manager.TeamMembers;
import vo.manager.TeamRebateOrInviteSearchVO;
import vo.manager.TeamRebateOrInviteSearchVO.MemberInfo;
import vo.manager.UserAmtDetailVO;
import vo.manager.UserFollowStatisticsSearchParamVO;
import vo.manager.UserFollowStatisticsVO;
import vo.manager.UserListSearchParamVO;
import vo.server.AliyunInitFaceVerifyParamVO;
import vo.server.AssetsDetailVO;
import vo.server.RegisterParamVO;
import vo.server.UserLoginFailedTimesVO;

/**
 * <p>
 * 会员信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

	@Resource
	private RedisDao redisDao;
	
	@Resource
	private CountryService countryService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private UserInfoMapper userInfoMapper;
	
	@Resource
	private AgentMemberInfoMapper agentMemberInfoMapper;
	
	@Resource
	private LevelDefService levelDefService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;
	
	/**
	 * 用户下级
	 * @param userId
	 * @return
	 */
	public List<ChildAndParentVO> pNextUsers(Integer userId) {
		List<ChildAndParentVO> uaLst = userInfoMapper.pNextUsers(userId);
		return uaLst;
	}
	
	/**
	 * 用户上级
	 * @param agentId
	 * @return
	 */
	public List<ChildAndParentVO> pNextAgents(Integer agentId) {
			List<ChildAndParentVO> uaLst = userInfoMapper.pNextAgents(agentId);
		return uaLst;
	}
	
	/**
	 * 管理系统->用户管理->用户列表->查询
	 */
	@Override
	public void managerUserList(Page<UserInfo> page, UserListSearchParamVO vo) {
		Set<Integer> ids = new HashSet<>();
		if(vo.getAgentId() != null && vo.getAgentId() > 0 && vo.isSearchChildOfAgent()) {
			List<ChildAndParentVO> auLst = this.agentMemberInfoMapper.agentMemberInfoList(vo.getAgentId());
			auLst.forEach(a->{
				ids.add(a.getChildId());
			});
		}
		userInfoMapper.managerUserList(page, vo, ids);
		List<UserInfo> list = page.getRecords();
		for(UserInfo i : list) {
			if(i.getUnavailableWithdrawalAmt().compareTo(i.getAvailableAmt()) == 1) {
				i.setUnavailableWithdrawalAmt(i.getAvailableAmt());
			}
			String key = RedisKeyPrefix.getOnlineUserKey(i.getId());
			OnlineUserVO onlineUser = redisDao.getBean( key, OnlineUserVO.class);
			if(onlineUser != null) {
				i.setIsOnline(true);
			}
		}
	}

	/**
	 * 管理系统->用户管理->用户列表->添加用户
	 */
	@Transactional
	@Override
	public Response<Void> managerUserAdd(AddUserParamVO vo, String ip, String operator) {
		//1、验证参数
		Integer agentId = 0;
		String regInvitationCode="";
		Integer generalAgentId = 0;
		UserInfo agent = this.getById(vo.getAgentId());
		if(agent != null) {
			agentId = agent.getId();
			//regInvitationCode = agent.getPromotionCode();
			generalAgentId = agent.getGeneralAgentId() == 0 ? agentId : agent.getGeneralAgentId();
		}
		if(vo.getAccountType() != Constant.ACCOUNT_TYPE_REAL && vo.getAccountType() != Constant.ACCOUNT_TYPE_VISUAL) {
			return Response.fail("账号类型不正确");
		}
		if (vo.getAccountType() == Constant.ACCOUNT_TYPE_REAL) {//实盘账户
			if(StringUtil.isEmpty(vo.getRealName()) || StringUtil.isEmpty(vo.getCertificateNumber())) {
				return Response.fail("实盘账户,必须填写真实姓名，真实证件号");
			}
		}
		if(StringUtil.isEmpty(vo.getAreaCode())) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(vo.getPhone())) {
			return Response.fail("请输入手机号");
		}
		if(!StringUtil.isNumber(vo.getPhone())) {
			return Response.fail("手机号码格式错误，请重新输入");
		}
		if(!StringUtil.limitLength(vo.getPhone(),7,11)) {
			return Response.fail("手机号码长度不符，请输入7-11位");
		}
		if(StringUtil.isEmpty(vo.getLoginPwd())) {
			return Response.fail("请输入登录密码");
		}
		if(!StringUtil.isEmpty(vo.getEmail())) {
			if(!StringUtil.isEmail(vo.getEmail())) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			if(this.lambdaQuery().eq(UserInfo::getEmail, vo.getEmail()).count() > 0) {
				return Response.fail("此邮箱账号已被注册，请重新输入");
			}
		}
		Country country = countryService.getCountryByAreaCode(vo.getAreaCode());
		if(country == null) {
			return Response.fail("地区编号错误，请重新选择");
		}
		if(this.lambdaQuery().eq(UserInfo::getAreaCode, vo.getAreaCode()).eq(UserInfo::getPhone, vo.getPhone()).count() > 0) {
			return Response.fail("此手机号已被注册，请重新输入");
		}
		//2、封装用户对象
		UserInfo newUser = new UserInfo();
		newUser.setNickname(StringUtil.newUserNickname());
		newUser.setAccountType(vo.getAccountType());
		newUser.setAgentId(agentId);
		newUser.setRegInvitationCode(regInvitationCode);
		newUser.setGeneralAgentId(generalAgentId);
		if(!StringUtil.isEmpty(vo.getRealName()) ) {
			newUser.setRealName(vo.getRealName());
		}
		if(!StringUtil.isEmpty(vo.getCertificateNumber())) {
			newUser.setCertificateNumber(vo.getCertificateNumber());
		}
		newUser.setAreaCode(vo.getAreaCode());
		newUser.setPhone(vo.getPhone());
		newUser.setEmail(vo.getEmail());
		newUser.setLoginPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, vo.getLoginPwd()));
		newUser.setRegIp(ip);
		newUser.setRegAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		//3、封装会员资料变更记录
		StringBuilder newContent = new StringBuilder();
		newContent.append("\n昵称：").append(newUser.getNickname());
		newContent.append("\n手机号：").append(newUser.getAreaCode()).append(newUser.getPhone());
		if(agent != null) {
			newContent.append("\n所属代理：").append(agent.getNickname()).append("(").append(agentId).append(")");
		}
		if(!StringUtil.isEmpty(vo.getEmail())) {
			newContent.append("\n邮箱：").append(vo.getEmail());
		}
		if(!StringUtil.isEmpty(vo.getRealName())) {
			newContent.append("\n真实姓名：").append(vo.getRealName());
		}
		if(!StringUtil.isEmpty(vo.getCertificateNumber())) {
			newContent.append("\n真实证件号：").append(vo.getCertificateNumber());
		}
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.ADD_USER.getCode());
		udcr.setNewContent(newContent.toString());
		udcr.setIp(ip);
		udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		udcr.setOperator(operator);
		//4、执行添加用户
		return doSaveUserInfo(newUser,udcr);
	}

	/**
	 * 管理系统->用户管理->用户列表->编辑用户
	 */
	@Override
	public Response<Void> managerUserEdit(EditUserParamVO vo, String ip, String operator) {
		//1、验证参数
		UserInfo old = this.getById(vo.getUserId());
		if(old == null) {
			return Response.fail("用户信息错误");
		}
		if(StringUtil.isEmpty(vo.getAreaCode())) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(vo.getPhone())) {
			return Response.fail("请输入手机号");
		}
		if(!StringUtil.isNumber(vo.getPhone())) {
			return Response.fail("手机号码格式错误，请重新输入");
		}
		if(!StringUtil.limitLength(vo.getPhone(),7,11)) {
			return Response.fail("手机号码长度不符，请输入7-11位");
		}
		Country country = countryService.getCountryByAreaCode(vo.getAreaCode());
		if(country == null) {
			return Response.fail("地区编号错误，请重新选择");
		}
		//2、封装用户对象
		StringBuilder oldContent = new StringBuilder();
		StringBuilder newContent = new StringBuilder();
		UserInfo updateUserInfo = new UserInfo();
		boolean flag = false;
		updateUserInfo.setId(vo.getUserId());
		if(!old.getAreaCode().equals(vo.getAreaCode()) || !old.getPhone().equals(vo.getPhone())) {
			if(this.lambdaQuery().eq(UserInfo::getAreaCode, vo.getAreaCode()).eq(UserInfo::getPhone, vo.getPhone()).ne(UserInfo::getId, old.getId()).count() > 0) {
				return Response.fail("此手机号已被注册，请重新输入");
			}
			oldContent.append("\n手机号码：").append(old.getAreaCode()).append(old.getPhone());
			newContent.append("\n手机号码：").append(vo.getAreaCode()).append(vo.getPhone());
			updateUserInfo.setAreaCode(vo.getAreaCode());
			updateUserInfo.setPhone(vo.getPhone());
			flag = true;
		}
		if(StringUtil.isEmpty(old.getEmail())) {
			if(!StringUtil.isEmpty(vo.getEmail())) {
				if(!StringUtil.isEmail(vo.getEmail())) {
					return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
				}
				if(this.lambdaQuery().eq(UserInfo::getEmail, vo.getEmail()).ne(UserInfo::getId, old.getId()).count() > 0) {
					return Response.fail("此邮箱账号已被注册，请重新输入");
				}
				oldContent.append("\n邮箱：无");
				newContent.append("\n邮箱：").append(vo.getEmail());
				updateUserInfo.setEmail(vo.getEmail());
				flag = true;
			}
		} else {
			if(StringUtil.isEmpty(vo.getEmail())) {
				oldContent.append("\n邮箱：").append(old.getEmail());
				newContent.append("\n邮箱：无");
				updateUserInfo.setEmail("");
				flag = true;
			} else if(!old.getEmail().equals(vo.getEmail())) {
				if(!StringUtil.isEmail(vo.getEmail())) {
					return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
				}
				if(this.lambdaQuery().eq(UserInfo::getEmail, vo.getEmail()).count() > 0) {
					return Response.fail("此邮箱账号已被注册，请重新输入");
				}
				oldContent.append("\n邮箱：").append(old.getEmail());
				newContent.append("\n邮箱：").append(vo.getEmail());
				updateUserInfo.setEmail(vo.getEmail());
				flag = true;
			}
		}
		if(!StringUtil.isEmpty(vo.getLoginPwd())) {
			String newloginPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, vo.getLoginPwd());
			oldContent.append("\n登录密码：").append("******");
			newContent.append("\n登录密码：").append("******");
			updateUserInfo.setLoginPwd(newloginPwd);
			flag = true;
		}
		if(!StringUtil.isEmpty(vo.getFundPwd())) {
			String newFundPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, vo.getFundPwd());
			oldContent.append("\n交易密码：").append("******");
			newContent.append("\n交易密码：").append("******");
			updateUserInfo.setLoginPwd(newFundPwd);
			flag = true;
		}
		if(vo.getLoginEnable() != null && vo.getLoginEnable() != old.getLoginEnable()) {
			oldContent.append("\n登录状态：").append(old.getLoginEnable() ? "可登录" : "不可登录");
			newContent.append("\n登录状态：").append(vo.getLoginEnable() ? "可登录" : "不可登录");
			updateUserInfo.setLoginEnable(vo.getLoginEnable());
			flag = true;
		}
		if(vo.getFollowEnable() != null && vo.getFollowEnable() != old.getFollowEnable()) {
			oldContent.append("\n跟投状态：").append(old.getFollowEnable() ? "可跟投" : "不可跟投");
			newContent.append("\n跟投状态：").append(vo.getFollowEnable() ? "可跟投" : "不可跟投");
			updateUserInfo.setFollowEnable(vo.getFollowEnable());
			flag = true;
		}
		//是否处理编辑
		if(flag) {
			//3、封装会员资料变更记录
			UserDataChangeRecord udcr = new UserDataChangeRecord();
			udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
			udcr.setOldContent(oldContent.toString());
			udcr.setNewContent(newContent.toString());
			udcr.setIp(ip);
			udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
			udcr.setOperator(operator);
			//4、执行保存操作
			return doSaveUserInfo(updateUserInfo, udcr);
		}
		return Response.success(); 
	}

	/**
	 * 强制下线
	 */
	@Override
	public Response<Void> forcedOffline(Integer userId) {
		UserInfo userInfo = this.getById(userId);
		if(userInfo == null) {
			return Response.fail("用户信息错误");
		}
		//踢用户下线
		String onlineUserKey = RedisKeyPrefix.getOnlineUserKey(userId);
		OnlineUserVO onlineUser = redisDao.getBean(onlineUserKey, OnlineUserVO.class);
		if(onlineUser != null) {
			redisDao.del(onlineUserKey);
			redisDao.del(RedisKeyPrefix.getTokenUserKey(onlineUser.getLatestToken()));
			for(String token : onlineUser.getTokenList()) {
				redisDao.del( RedisKeyPrefix.getTokenUserKey(token));
			}
		}
		return Response.success();
	}

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
	@Override
	@Transactional
	public void updateUserAvailableAmt(Integer userId,  AmtDeTypeEnum deType, BigDecimal amt, String deSummary, CurrencyEnum currency, BigDecimal exchangeRate, String ip, String ipAddress, String operator,String businessOrderSn) {
		if (amt.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
		BigDecimal deAmt, deForeignAmt;
		switch(currency) {
		case CNY:
		default:
			deAmt = amt;
			deForeignAmt = null;
			break;
		case HKD:
		case USD:
			deAmt = amt.divide(exchangeRate, 2, RoundingMode.HALF_UP);
			deForeignAmt = amt;
			break;
		}
		UserInfo currentUser = this.getById(userId);
		StringBuilder setSql = new StringBuilder();
		BigDecimal beAmt = currentUser.getAvailableAmt();
		BigDecimal afAmt = currentUser.getAvailableAmt();
		BigDecimal beTradingFrozenAmt = currentUser.getTradingFrozenAmt();
		BigDecimal afTradingFrozenAmt = currentUser.getTradingFrozenAmt();
		//BigDecimal beIpoAmt = currentUser.getIpoAmt();
		//BigDecimal afIpoAmt = currentUser.getIpoAmt();
		BigDecimal beFollowAmt = currentUser.getFollowAmt();
		BigDecimal afFollowAmt = currentUser.getFollowAmt();
		switch(deType) {
		case ManualRecharge:
		case ClosingPosition:
		case ReturnFromNewShare:
		case BankCardWithdrawalDeclined:
		case BankCardWithdrawalCnnel:
		case ReBateSettement:
		case FinishFollowRecord://结束跟投记录-增加可用金额
		default:
			setSql.append("available_amt=available_amt+").append(deAmt);
			afAmt = afAmt.add(deAmt);
			break;
		case BankCardWithdrawalApplication:
		case ManualDeduction:
		case PayNewShare:
		case UserInterest:
		case FinancingRepayment:
			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
			afAmt = afAmt.subtract(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case BuyStock:
			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
			setSql.append(",trading_frozen_amt=trading_frozen_amt+").append(deAmt);
			afAmt = afAmt.subtract(deAmt);
			afTradingFrozenAmt = afTradingFrozenAmt.add(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case TransferPosition:
			setSql.append("trading_frozen_amt=case when (trading_frozen_amt-").append(deAmt).append(")<0 then 0 else (trading_frozen_amt-").append(deAmt).append(") end");
			afTradingFrozenAmt = afTradingFrozenAmt.subtract(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case CanneledPending:
		case RejectedPending:
			setSql.append("available_amt=available_amt+").append(deAmt);
			setSql.append(",trading_frozen_amt=case when (trading_frozen_amt-").append(deAmt).append(")<0 then 0 else (trading_frozen_amt-").append(deAmt).append(") end");
			afAmt = afAmt.add(deAmt);
			afTradingFrozenAmt = afTradingFrozenAmt.subtract(deAmt);
			break;
//		case SubscripNewShare:
//			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
//			setSql.append(",ipo_amt=ipo_amt+").append(deAmt);
//			afAmt = afAmt.subtract(deAmt);
//			afIpoAmt = afIpoAmt.add(deAmt);
//			deAmt = BigDecimal.ZERO.subtract(deAmt);
//			break;
//		case SubscripNewShareFail:
//			setSql.append("ipo_amt=case when (ipo_amt-").append(deAmt).append(")<0 then 0 else (ipo_amt-").append(deAmt).append(") end");
//			setSql.append(",available_amt=available_amt+").append(deAmt);
//			afAmt = afAmt.add(deAmt);
//			afIpoAmt = afIpoAmt.subtract(deAmt);
//			break;
//		case WinNewShare:
//			setSql.append("ipo_amt=case when (ipo_amt-").append(deAmt).append(")<0 then 0 else (ipo_amt-").append(deAmt).append(") end");
//			afIpoAmt = afIpoAmt.subtract(deAmt);
//			deAmt = BigDecimal.ZERO.subtract(deAmt);
//			break;
		case ApplyFollowRequest://申请跟投审核
			//扣除用户可用金额
			setSql.append("available_amt=case when (available_amt-").append(deAmt).append(")<0 then 0 else (available_amt-").append(deAmt).append(") end");
			//增加跟投金额
			setSql.append(",follow_amt=follow_amt+").append(deAmt);
			afAmt = afAmt.subtract(deAmt);
			afFollowAmt = afFollowAmt.add(deAmt);
			deAmt = BigDecimal.ZERO.subtract(deAmt);
			break;
		case RefuseFollowRequest://拒绝跟投审核
			//扣除跟投金额
			setSql.append("follow_amt=case when (follow_amt-").append(deAmt).append(")<0 then 0 else (follow_amt-").append(deAmt).append(") end");
			//增加用户可用金额
			setSql.append(",available_amt=available_amt+").append(deAmt);
			afAmt = afAmt.add(deAmt);
			afFollowAmt = afFollowAmt.subtract(deAmt);
			break;
		case FinishFollowRecord_reduce_follow_amt://结束跟投记录-减扣跟投资金
			//扣除跟投金额
			setSql.append("follow_amt=case when (follow_amt-").append(deAmt).append(")<0 then 0 else (follow_amt-").append(deAmt).append(") end");
			afFollowAmt = afFollowAmt.subtract(deAmt);
			break;
		}
		UpdateWrapper<UserInfo> uw = new UpdateWrapper<>();
		uw.setSql(setSql.toString());
		uw.eq("id", userId);
		this.update(uw);
		UserAmtChangeRecord ucr = new UserAmtChangeRecord();
		ucr.setUserId(userId);
		ucr.setDeType(deType.getCode());
		ucr.setDeTypeName(deType.getName());
		ucr.setBeAmt(beAmt);
		ucr.setAfAmt(afAmt);
		ucr.setDeCnyAmt(deAmt);
		ucr.setDeForeignAmt(deForeignAmt);
		ucr.setBeTradingFrozenAmt(beTradingFrozenAmt);
		ucr.setAfTradingFrozenAmt(afTradingFrozenAmt);
		//ucr.setBeIpoAmt(beIpoAmt);
		//ucr.setAfIpoAmt(afIpoAmt);
		ucr.setBeFollowAmt(beFollowAmt);
		ucr.setAfFollowAmt(afFollowAmt);
		ucr.setDeSummary(deSummary);
		ucr.setCurrency(currency.getCode());
		ucr.setAddIp(ip);
		ucr.setAddAddress(ipAddress);
		ucr.setOperator(operator);
		ucr.setExchangeRate(exchangeRate);
		ucr.setBusinessOrderSn(businessOrderSn);
		ucr.insert();
	}
	
	/**
	 * 获取用户资金信息
	 */
	@Override
	public UserAmtDetailVO getUserAmtDetail(Integer userId) {
		UserAmtDetailVO vo = new UserAmtDetailVO();
		UserInfo user = this.getById(userId);
		if(user == null) {
			return vo;
		}
		vo.setUserId(user.getId());
		vo.setAccountType(user.getAccountType());
		vo.setAvailableAmt(user.getAvailableAmt());
		BigDecimal unavailableWithdrawalAmt = this.userInfoMapper.getUserUnavailableWithdrawalAmt(userId);
		if(unavailableWithdrawalAmt.compareTo(user.getAvailableAmt()) == 1) {
			unavailableWithdrawalAmt = user.getAvailableAmt();
		}
		vo.setAvailableWithdrawalAmt(user.getAvailableAmt().subtract(unavailableWithdrawalAmt));
		vo.setDetentionAmt(user.getDetentionAmt());
		vo.setTradingFrozenAmt(user.getTradingFrozenAmt());
		vo.setFollowAmt(user.getFollowAmt());
		vo.setAgentId(user.getAgentId());
		if(user.getAgentId() > 0) {
			UserInfo agentInfo = this.getById(user.getAgentId());
			vo.setAgentName(agentInfo.getNickname());
		}
		BigDecimal totalAmt = user.getAvailableAmt().add(user.getTradingFrozenAmt()).add(user.getFollowAmt()).subtract(user.getDetentionAmt());
		vo.setTotalAmt(totalAmt);
		vo.setUsdExchangeRate(this.sysParamConfigService.getExchangeRate(CurrencyEnum.USD));
		vo.setHkdExchangeRate(this.sysParamConfigService.getExchangeRate(CurrencyEnum.HKD));
		return vo;
	}

	/**
	 * 用户注册验证
	 */
	@Override
	public Response<Void> registerVerify(RegisterParamVO param) {
		if(StringUtil.isEmpty(param.getAreaCode())) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(param.getPhone()) || !StringUtil.isNumber(param.getPhone())) {
			return Response.fail("手机号码格式错误，请重新输入");
		}
		if(!StringUtil.limitLength(param.getPhone(),7,11)) {
			return Response.fail("手机号码长度不符，请输入7-11位");
		}
		if(StringUtil.isEmpty(param.getInvitationCode())) {
			return Response.fail("请输入邀请码");
		}
		if (!StringUtil.isEmpty(param.getEmail())) {
			if (!StringUtil.isEmail(param.getEmail())) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			int emailCount =this.lambdaQuery().eq(UserInfo::getEmail, param.getEmail()).count();
			if (emailCount > 0) {
				return Response.fail("该邮箱已被注册");
			}
		}
		Country country = countryService.getCountryByAreaCode(param.getAreaCode());
		if(country == null) {
			return Response.fail("地区编号错误，请重新选择");
		}
		int count = this.lambdaQuery().eq(UserInfo::getAreaCode, param.getAreaCode()).eq(UserInfo::getPhone, param.getPhone()).count();
		if(count > 0) {
			return Response.fail("该手机号已被注册");
		}
		UserInfo agentUserInfo = this.lambdaQuery().eq(UserInfo::getPromotionCode, param.getInvitationCode()).one();
		if (agentUserInfo != null ) {
			if (!agentUserInfo.getPromotionEnable()) {
				return Response.fail("邀请码:"+param.getInvitationCode()+"不允许邀请");
			}
		}else {
			return Response.fail("邀请码不正确，请重新输入");
		}
		return Response.success();
	}

	/**
	 * 用户注册
	 */
	@Override
	@Transactional
	public Response<Void> register(RegisterParamVO param, String ip) {
		//1、验证参数
		if (!registerVerify(param).getCode().equals(Response.success().getCode())){
			return registerVerify(param);
		}
		if(StringUtil.isEmpty(param.getLoginPwd())) {
			return Response.fail("请输入登录密码");
		}
		if(param.getLoginPwd().length() < 8 || param.getLoginPwd().length() > 12 || !StringUtil.isNumberAndEnglish(param.getLoginPwd())) {
			return Response.fail("密码长度为8-12个字符，且必须包含字母和数字");
		}
		if (StringUtil.isEmpty(param.getRealName())) {
			return Response.fail("请输入真实姓名");
		}
		if (StringUtil.isEmpty(param.getCertificateNumber())) {
			return Response.fail("请输入真实身份证号");
		}
		UserInfo agent = this.lambdaQuery().eq(UserInfo::getPromotionCode, param.getInvitationCode()).one();
		if (agent == null) {
			return Response.fail("邀请码不正确");
		}
		//2、封装用户对象
		UserInfo newUser = new UserInfo();
		newUser.setNickname(StringUtil.newUserNickname());
		newUser.setAccountType(Constant.ACCOUNT_TYPE_REAL);
		newUser.setAgentId(agent.getId());
		newUser.setRegInvitationCode(agent.getPromotionCode());
		newUser.setGeneralAgentId(agent.getGeneralAgentId());
		newUser.setRealName(param.getRealName());
		newUser.setCertificateNumber(param.getCertificateNumber());
		newUser.setAreaCode(param.getAreaCode());
		newUser.setPhone(param.getPhone());
		newUser.setEmail(param.getEmail());
		newUser.setLoginPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, param.getLoginPwd()));
		newUser.setRegIp(ip);
		newUser.setRegAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		//3、封装会员资料变更记录
		StringBuilder newContent = new StringBuilder();
		newContent.append("\n昵称：").append(newUser.getNickname());
		newContent.append("\n手机号：").append(newUser.getAreaCode()).append(newUser.getPhone());
		if(agent != null) {
			newContent.append("\n所属代理：").append(agent.getNickname()).append("(").append(agent.getId()).append(")");
		}
		if(!StringUtil.isEmpty(param.getEmail())) {
			newContent.append("\n邮箱：").append(param.getEmail());
		}
		if(!StringUtil.isEmpty(param.getRealName())) {
			newContent.append("\n真实姓名：").append(param.getRealName());
		}
		if(!StringUtil.isEmpty(param.getCertificateNumber())) {
			newContent.append("\n真实证件号：").append(param.getCertificateNumber());
		}
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.USER_REGISTER.getCode());
		udcr.setNewContent(newContent.toString());
		udcr.setIp(ip);
		udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		udcr.setOperator(newUser.getOperator());
		//4、执行用户保存操作
		return doSaveUserInfo(newUser,udcr);
	}
	
	/**
	 * 会员登录
	 */
	@Override
	public Response<TokenUserVO> login(String areaCode, String loginAccount, String loginPwd, int loginType, String ip,
			String requestUrl) {
		if(StringUtil.isEmpty(loginAccount)) {
			return Response.fail("请输入您的账号");
		}
		if(StringUtil.isEmpty(loginPwd)) {
			return Response.fail("请输入登录密码");
		}
		
		LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
		switch(loginType) {
		case 1:
			if(StringUtil.isEmpty(areaCode)) {
				return Response.fail("请选择地区编号");
			}
			if(!StringUtil.isNumber(loginAccount)) {
				return Response.fail("手机号码格式错误，请重新输入");
			}
			lqw.eq(UserInfo::getAreaCode, areaCode);
			lqw.eq(UserInfo::getPhone, loginAccount);
			break;
		case 2:
			if(!StringUtil.isEmail(loginAccount)) {
				return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
			}
			lqw.eq(UserInfo::getEmail, loginAccount);
			break;
		default:
			if(!StringUtil.isNumber(loginAccount)) {
				return Response.fail("登录id的格式错误，请重新输入");
			}
			lqw.eq(UserInfo::getId, loginAccount);
			break;
		}
		UserInfo me = this.getOne(lqw);
		if(me == null) {
			return Response.fail("账号或者密码错误");
		} 
		
		String key = RedisKeyPrefix.getUserLoginFailedTimesKey(me.getId());
		UserLoginFailedTimesVO  ulf = redisDao.getBean(key, UserLoginFailedTimesVO.class);
		
		StockParamConfig spc = sysParamConfigService.getSysParamConfig();
		
		if(ulf != null && spc.getMaxTimesOfIncoreectPassword() > 0 && ulf.getTimes() >= spc.getMaxTimesOfIncoreectPassword()) {
  			return Response.fail("账号或密码连续错误" + ulf.getTimes() + "次，需24小时后才能使用账户密码登录");
		}
		
		
		IpAddress ia = ipAddressService.getIpAddress(ip);
		String ipAddress = ia.getAddress2();
		Date now = new Date();
		loginPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, loginPwd);
		if(!me.getLoginPwd().equals(loginPwd)) {
			if(ulf == null) {
				ulf = new UserLoginFailedTimesVO();
			}
			ulf.setTimes(ulf.getTimes() + 1);
			redisDao.setBean(key, ulf, 24, TimeUnit.HOURS);
			//登录日志
			UserLoginLog ull = new UserLoginLog();
			ull.setUserId(me.getId());
			ull.setOperateStatus(1);
			ull.setOperateTime(now);
			ull.setIp(ip);
			ull.setIpAddress(ipAddress);
			ull.setRequestUrl(requestUrl);
			ull.insert();
			if(spc.getMaxTimesOfIncoreectPassword() > 0) {
				if (ulf.getTimes() >= spc.getMaxTimesOfIncoreectPassword()) {
					return Response.fail("账号或密码连续错误" + ulf.getTimes() + "次，需24小时后才能使用账户密码登录");
				}
				return Response.fail("账号或密码错误，" + (spc.getMaxTimesOfIncoreectPassword() - ulf.getTimes()) + "次后该账号将被锁定");
				
			}
			return Response.fail("账号或密码错误");
		}
		if(!me.getLoginEnable()) {
			//登录日志
			UserLoginLog ull = new UserLoginLog();
			ull.setUserId(me.getId());
			ull.setOperateStatus(2);
			ull.setOperateTime(now);
			ull.setIp(ip);
			ull.setIpAddress(ipAddress);
			ull.setRequestUrl(requestUrl);
			ull.insert();
			return Response.fail("您的账户已被限制登录，详情原因请咨询客服");
		} 
		redisDao.del(RedisKeyPrefix.getUserLoginFailedTimesKey(me.getId()));
		String token = PasswordGenerator.createToken(me.getId(), System.currentTimeMillis());
		String tokenKey = RedisKeyPrefix.getTokenUserKey(token);
		TokenUserVO tokenUser = new TokenUserVO();
		tokenUser.setLoginId(me.getId());
		tokenUser.setAccountType(me.getAccountType());
		tokenUser.setToken(token);
		redisDao.setBean(tokenKey, tokenUser, 30, TimeUnit.MINUTES);
		String onlineUserKey = RedisKeyPrefix.getOnlineUserKey(me.getId());
		OnlineUserVO ou = redisDao.getBean(onlineUserKey, OnlineUserVO.class);
		if (ou == null) {
			ou = new OnlineUserVO();
		}
		ou.setLatestToken(token);
		ou.setCurrentTimestamp(System.currentTimeMillis());
		ou.getTokenList().add(token);
		redisDao.setBean(onlineUserKey, ou, 30, TimeUnit.MINUTES);
		this.lambdaUpdate().set(UserInfo::getLastLoginTime, now)
			.set(UserInfo::getLastLoginIp, ip)
			.set(UserInfo::getLastLoginAddress, ipAddress)
			.eq(UserInfo::getId, me.getId()).update();
		//登录日志
		UserLoginLog ull = new UserLoginLog();
		ull.setUserId(me.getId());
		ull.setOperateTime(now);
		ull.setIp(ip);
		ull.setIpAddress(ipAddress);
		ull.setRequestUrl(requestUrl);
		ull.insert();
		return Response.successData(tokenUser);
	}

	/**
	 * 会员登出
	 */
	@Override
	public Response<Void> logout(Integer userId, String ip, String requestUrl) {
		String onlineUserKey = RedisKeyPrefix.getOnlineUserKey(userId);
		OnlineUserVO ou = redisDao.getBean(onlineUserKey, OnlineUserVO.class);
		if (ou != null) {
			String token = ou.getLatestToken();
			String tokenKey = RedisKeyPrefix.getTokenUserKey(token);
			redisDao.del(tokenKey);
			redisDao.del(onlineUserKey);
		}
		IpAddress ia = ipAddressService.getIpAddress(ip);
		String ipAddress = ia.getAddress2();
		UserLoginLog ull = new UserLoginLog();
		ull.setUserId(userId);
		ull.setLoginType(1);
		ull.setOperateStatus(0);
		ull.setOperateTime(new Date());
		ull.setIp(ip);
		ull.setIpAddress(ipAddress);
		ull.setRequestUrl(requestUrl);
		ull.insert();
		return Response.success();
	}

	/**
	 * 阿里云-发起人脸认证
	 */
	@Override
	public Response<ResultObject> doAliyunInitFaceVerify(AliyunInitFaceVerifyParamVO param) {
		String certName = "";//真实姓名
		String certNo ="";//真实证件号
		if (param.getVerifyType() == 0) {//注册人脸认证(未注册用户，注册+人脸认证)
			if (param.getRegisterParamVO() == null) {
				return Response.fail("registerParamVO:注册参数，不能为空");
			}
			if (StringUtil.isEmpty(param.getRegisterParamVO().getRealName())) {
				return Response.fail("registerParamVO:注册参数-真实姓名，不能为空");
			}
			if (StringUtil.isEmpty(param.getRegisterParamVO().getCertificateNumber())) {
				return Response.fail("registerParamVO:注册参数-证件号，不能为空");
			}
			certName = param.getRegisterParamVO().getRealName();
			certNo = param.getRegisterParamVO().getCertificateNumber();
		}else {//其他人脸认证，必须是注册用户
			if (param.getUserInfo() == null) {
				return Response.fail("用户未注册，请先注册"); 
			}
			if (!param.getUserInfo().isFaceVerify()) {
				return Response.fail("该用户未进行过人脸认证"); 
			}
			certName = param.getUserInfo().getRealName();
			certNo = param.getUserInfo().getCertificateNumber();
		}
		//获取-金融级人脸验证方案(【身份证、姓名】检测认证)-请求参数
		InitFaceVerifyRequest initFaceVerifyRequestIdpro = InitFaceVerify.getInitFaceVerifyRequestIdpro(certName, certNo, param.getReturnUrl(), param.getMetaInfo());
		//执行-金融级活体检测-发起认证请求
		try {
			InitFaceVerifyResponse initFaceVerifyResponse = InitFaceVerify.doInitFaceVerify(initFaceVerifyRequestIdpro);
			if (initFaceVerifyResponse.getBody().getCode().equals("200") && 
					initFaceVerifyResponse.getBody().getResultObject().getCertifyId() != null) {
				//存入人脸认证缓存
				String key = RedisKeyPrefix.getAliyunInitFaceVerifyParamVOKey(initFaceVerifyResponse.getBody().getResultObject().getCertifyId());
				redisDao.setBean(key, param, 5, TimeUnit.MINUTES);//5分钟销毁
				return Response.successData(initFaceVerifyResponse.getBody().getResultObject());
			}else {
				return Response.fail(JSONUtil.toJsonStr(initFaceVerifyResponse.getBody().getMessage()));
			}
		} catch (Exception e) {
			log.error("阿里云-发起人脸认证-失败:"+ e);
			return Response.fail("阿里云-发起人脸认证-失败,请联系开发人员");
		}
	}

	/**
	 * 阿里云-获取人脸认证结果
	 */
	@Override
	public Response<String> doAliyunDescribeFaceVerify(String certifyId) {
		try {
			DescribeFaceVerifyResponse describeFaceVerifyResponse = DescribeFaceVerify.doDescribeFaceVerify(certifyId);
			if (describeFaceVerifyResponse.getBody().getCode().equals("200") &&
					describeFaceVerifyResponse.getBody().getResultObject().getSubCode() != null ) {
				return doFaceVerifyBusiness(certifyId,describeFaceVerifyResponse.getBody().getResultObject().getSubCode());
			}else {
				return Response.fail(JSONUtil.toJsonStr(describeFaceVerifyResponse.getBody().getMessage()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("阿里云-获取人脸认证结果-失败:{}", e);
			return Response.fail("阿里云-获取人脸认证结果-失败,请联系开发人员");
		}
	}

	/**
	 * 执行-人脸认证业务
	 */
	@Override
	public Response<String> doFaceVerifyBusiness(String certifyId, String passed) {
		//获取实人认证结果code消息
		String message = AliyunFaceUtil.getDescribeFaceVerifyCodeMessage(passed);
		//获取人脸认证缓存
		String key = RedisKeyPrefix.getAliyunInitFaceVerifyParamVOKey(certifyId);
		AliyunInitFaceVerifyParamVO aliyunInitFaceVerifyParamVO = redisDao.getBean(key, AliyunInitFaceVerifyParamVO.class);
		//是否要登录
		boolean loginFalg = false;
		if (aliyunInitFaceVerifyParamVO == null) {
			return Response.fail("人脸认证-已过期");
		}
		//根据人脸认证缓存处理状态处理业务
		switch (aliyunInitFaceVerifyParamVO.getVerifyPassState()) {
		case 0://发起认证的数据
			Long expire = redisDao.getExpire(key, TimeUnit.MINUTES);//获取人脸缓存时间
			if (passed.equals("200")) {//认证成功
				aliyunInitFaceVerifyParamVO.setVerifyPassState(1);
				//修改缓存
				redisDao.setBean(key, aliyunInitFaceVerifyParamVO,expire,TimeUnit.MINUTES);
				//处理认证后相关业务
				if(aliyunInitFaceVerifyParamVO.getVerifyType() == 0) {//处理注册
					//执行注册
					Response<Void> ResponseRegister = register(aliyunInitFaceVerifyParamVO.getRegisterParamVO(), aliyunInitFaceVerifyParamVO.getIp());
					if (!ResponseRegister.getCode().equals(Response.success().getCode())){
						return Response.fail(ResponseRegister.getMsg()) ;
					}
					loginFalg = true;//需要登录
				}
			}else {//认证失败
				aliyunInitFaceVerifyParamVO.setVerifyPassState(2);
				//修改缓存
				redisDao.setBean(key, aliyunInitFaceVerifyParamVO,expire,TimeUnit.MINUTES);
				return Response.fail(message);
			}
			break;
		case 1://认证成功
			if(aliyunInitFaceVerifyParamVO.getVerifyType() == 0) {//处理注册
				loginFalg = true;//需要登录
			}
			break;
		default://认证失败
			return Response.fail(message);
		}
		//处理需要登录业务
		if(loginFalg) {
			//执行登录
			Response<TokenUserVO> ResponseLogin = login(aliyunInitFaceVerifyParamVO.getRegisterParamVO().getAreaCode(), aliyunInitFaceVerifyParamVO.getRegisterParamVO().getPhone(), aliyunInitFaceVerifyParamVO.getRegisterParamVO().getLoginPwd(), 1, aliyunInitFaceVerifyParamVO.getIp(), aliyunInitFaceVerifyParamVO.getRequestUrl());
			if (!ResponseLogin.getCode().equals(Response.success().getCode())){
				return Response.fail(ResponseLogin.getMsg()) ;
			}
			//返回用户token
			String userToken = ResponseLogin.getData().getToken();
			return Response.successData(userToken,userToken);
		}
		return Response.success(Response.success().getMsg());
	}

	/**
	 * 忘记密码(人脸认证-修改密码)
	 */
	@Override
	public Response<Void> doFaceVerifyModifyLoginPwd(String certifyId, String newLoginPwd, String ip) {
		//获取人脸认证缓存
		String key = RedisKeyPrefix.getAliyunInitFaceVerifyParamVOKey(certifyId);
		AliyunInitFaceVerifyParamVO aliyunInitFaceVerifyParamVO = redisDao.getBean(key, AliyunInitFaceVerifyParamVO.class);
		if (aliyunInitFaceVerifyParamVO == null) {
			return Response.fail("人脸认证-已过期");
		}
		switch (aliyunInitFaceVerifyParamVO.getVerifyPassState()) {
		case 0:
			return Response.fail("暂未进行人脸认证");
			
		case 1:
			UserInfo userInfo = aliyunInitFaceVerifyParamVO.getUserInfo();
			return doModifyLoginPwd(userInfo.getId(), newLoginPwd, ip, userInfo.getOperator());	
			
		default:
			return Response.fail("人脸认证失败");
		}
	}

	/**
	 * 设置-安全设置-修改手机号码
	 */
	@Override
	@Transactional
	public Response<Void> modifyPhone(Integer userId, String areaCode, String phone, String ip, String operator) {
		//1、验证参数
		if(StringUtil.isEmpty(areaCode)) {
			return Response.fail("请选择地区编号");
		}
		if(StringUtil.isEmpty(phone)) {
			return Response.fail("请输入手机号码");
		}
		if(!StringUtil.isNumber(phone)) {
			return Response.fail("手机号格式错误，请重新输入");
		}
		Country country = countryService.getCountryByAreaCode(areaCode);
		if(country == null) {
			return Response.fail("地区编号错误");
		}
		if(this.lambdaQuery().eq(UserInfo::getAreaCode, areaCode).eq(UserInfo::getPhone, phone).ne(UserInfo::getId, userId).count() > 0) {
			return Response.fail("此手机号已被注册，请重新输入");
		}
		UserInfo old = this.getById(userId);
		if(old == null) {
			return Response.fail("用户信息错误");
		}
		String oldMobilePhone = old.getAreaCode() + old.getPhone();
		String newMobilePhone = areaCode + phone;
		//2、封装用户对象
		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setId(userId);
		updateUserInfo.setAreaCode(areaCode);
		updateUserInfo.setPhone(phone);		
		//3、封装会员资料变更记录
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setOldContent("手机号码：" + oldMobilePhone);
		udcr.setNewContent("手机号码：" + newMobilePhone);
		udcr.setIp(ip);
		udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		udcr.setOperator(operator);
		//4、执行保存操作
		return doSaveUserInfo(updateUserInfo, udcr);
		
	}

	/**
	 * 设置-安全设置-修改邮箱
	 */
	@Override
	@Transactional
	public Response<Void> modifyEmail(Integer userId, String emmail, String ip, String operator) {
		//1、验证参数
		if(!StringUtil.isEmail(emmail)) {
			return Response.fail("请输入正确的邮箱，例如：example@gmail.com");
		}
		if(this.lambdaQuery().eq(UserInfo::getEmail, emmail).ne(UserInfo::getId, userId).count() > 0) {
			return Response.fail("此邮箱账号已被注册，请重新输入");
		}
		UserInfo old = this.getById(userId);
		if(old == null) {
			return Response.fail("用户信息错误");
		}
		//2、封装用户对象
		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setId(userId);
		updateUserInfo.setEmail(emmail);		
		//2、封装会员资料变更记录
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setOldContent("邮箱：" + (StringUtil.isEmpty(old.getEmail()) ? "" : old.getEmail()));
		udcr.setNewContent("邮箱：" + emmail);
		udcr.setIp(ip);
		udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		udcr.setOperator(operator);
		//3、执行保存操作
		return doSaveUserInfo(updateUserInfo, udcr);
	}

	/**
	 * 设置-安全设置-修改登录密码
	 */
	@Override
	@Transactional
	public Response<Void> modifyLoginPwd(Integer userId, String oldLoginPwd, String newLoginPwd, String ip,
			String operator) {
		UserInfo ui = this.getById(userId);
		oldLoginPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, oldLoginPwd);
		if(!ui.getLoginPwd().equals(oldLoginPwd)) {
			return Response.fail("原登录密码验证失败，请重新输入");
		}
		return doModifyLoginPwd(userId, newLoginPwd, ip, operator);
	}

	/**
	 * 设置-安全设置-修改交易密码
	 */
	@Override
	@Transactional
	public Response<Void> modifyFundPwd(Integer userId, String oldPwd, String newFundPwd, String ip, String operator) {
		//1、验证参数
		UserInfo old = this.getById(userId);
		if(old == null) {
			return Response.fail("用户信息错误");
		}
		String oldFundPwd = old.getFundPwd();
		oldPwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, oldPwd);
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		if(StringUtil.isEmpty(oldFundPwd)) {
			if(!oldPwd.equals(old.getLoginPwd())) {
				return Response.fail("原登录密码验证失败，请重新输入");
			}
			udcr.setOldContent("交易密码：");
		} else {
			if(!oldPwd.equals(old.getFundPwd())) {
				return Response.fail("原交易密码验证失败，请重新输入");
			}
			udcr.setOldContent("交易密码：*******");
		}
		//2、封装用户对象
		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setId(userId);
		updateUserInfo.setFundPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, newFundPwd));		
		//2、封装会员资料变更记录
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setNewContent("交易密码：******");
		udcr.setIp(ip);
		udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		udcr.setOperator(operator);
		//3、执行保存操作
		return doSaveUserInfo(updateUserInfo, udcr);
	}

	/**
	 * 设置-安全设置-密码验证
	 */
	@Override
	@Transactional
	public Response<Void> pwdVerify(Integer userId, String pwd, int pwdType) {
		UserInfo ui = this.getById(userId);
		pwd = PasswordGenerator.generate(Constant.PASSWORD_PREFIX, pwd);
		switch (pwdType) {
		case 1: //交易密码
			if(StringUtil.isEmpty(ui.getFundPwd())) {
				return Response.fail("交易密码未设置，请验证登录密码");
			} else {
				if(!pwd.equals(ui.getFundPwd())) {
					return Response.fail("交易密码验证失败，请重新输入");
				}
			}
			break;

		default: //登录密码
			if(!pwd.equals(ui.getLoginPwd())) {
				return Response.fail("登录密码验证失败，请重新输入");
			}
			break;
		}
		return Response.success();
	}
	
	/**
	 * 执行修改密码业务逻辑
	 * @param userId
	 * @param newLoginPwd
	 * @param ip
	 * @param operator
	 * @return
	 */
	@Transactional
	public Response<Void> doModifyLoginPwd(Integer userId,String newLoginPwd,String ip,String operator){
		//1、验证参数
		UserInfo old = this.getById(userId);
		if(old == null) {
			return Response.fail("用户信息错误");
		}
		//1、封装用户对象
		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setId(userId);
		updateUserInfo.setLoginPwd(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, newLoginPwd));		
		//2、封装会员资料变更记录
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.EDIT_USER.getCode());
		udcr.setOldContent("登录密码：******");
		udcr.setNewContent("登录密码：******");
		udcr.setIp(ip);
		udcr.setIpAddress(this.ipAddressService.getIpAddress(ip).getAddress2());
		udcr.setOperator(operator);
		//3、执行保存操作
		return doSaveUserInfo(updateUserInfo, udcr);
	}

	/**
	 * 资产明细
	 */
	@Override
	public Response<AssetsDetailVO> assetsDetail(Integer userId) {
		AssetsDetailVO vo = new AssetsDetailVO();
		UserInfo ui = this.getById(userId);
		vo.setAvailableAmt(ui.getAvailableAmt());
		vo.setDetentionAmt(ui.getDetentionAmt());
		vo.setFollowAmt(ui.getFollowAmt());
		vo.setTradingFrozenAmt(ui.getTradingFrozenAmt());
		vo.setAvailableWithdrawalAmt(ui.getAvailableAmt());
		BigDecimal totalAssets = ui.getAvailableAmt().subtract(ui.getDetentionAmt()).add(ui.getFollowAmt()).add(ui.getTradingFrozenAmt());
		BigDecimal totalMarketValue = BigDecimal.ZERO; 
		BigDecimal totalProfit = BigDecimal.ZERO; 
		BigDecimal todayProfit = BigDecimal.ZERO; 
		BigDecimal totalHoldingValue = BigDecimal.ZERO; //总成本
		LambdaQueryWrapper<UserStockPosition> lqw = new LambdaQueryWrapper<>();
		lqw.select(UserStockPosition::getStockCode, UserStockPosition::getStockType, UserStockPosition::getBuyingPrice, UserStockPosition::getBuyingShares, UserStockPosition::getPositionDirection);
		lqw.eq(UserStockPosition::getUserId, userId);
		lqw.eq(UserStockPosition::getPositionStatus, 2);
		List<UserStockPosition> list = this.userStockPositionService.list(lqw);
		if(list.size() > 0) {
			List<String> stockGids = new ArrayList<>();
			List<String> hkOrUsStockGids = new ArrayList<>();
			for(UserStockPosition i : list) {
				if(i.getStockType().equals(StockTypeEnum.BJ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SZ.getCode()) 
						|| i.getStockType().equals(StockTypeEnum.SH.getCode())) {
					stockGids.add(i.getStockType() + i.getStockCode());
				} else {
					hkOrUsStockGids.add(i.getStockType() + i.getStockCode());
				}
			}
			if(stockGids.size() > 0) {
				 List<StockQuotesVO> stockQuotesVOList = SinaApi.getSinaStocks(stockGids);
				 for(StockQuotesVO stockQuotesVO : stockQuotesVOList) {
					 for(UserStockPosition i : list) { 
						 if(stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							 BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());//持有股数
							 BigDecimal holdingValue = i.getBuyingPrice().multiply(buyingShares);//持有成本=买入价格*持有股数
							 BigDecimal marketValue = stockQuotesVO.getNowPrice().multiply(buyingShares);//持有市值=当前价格*持有股数
							 totalMarketValue = totalMarketValue.add(marketValue);//持仓总市值
							 totalHoldingValue = totalHoldingValue.add(holdingValue);//持仓总总成本
							 if(i.getPositionDirection() == Constant.GO_LONG) {
								 totalProfit = totalProfit.add(marketValue.subtract(holdingValue));
								 todayProfit = todayProfit.add(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							 } else {
								 totalProfit = totalProfit.add(holdingValue.subtract(marketValue));
								 todayProfit = todayProfit.subtract(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							 }
						 }
					 }
				 }
			}
			if (hkOrUsStockGids.size() > 0) {
				BigDecimal hkExchangeRate = null, usExchangeRate = null;
				List<StockQuotesVO> stockQuotesVOList = sysParamConfigService.getStockRealTimeList(hkOrUsStockGids);
				for (StockQuotesVO stockQuotesVO : stockQuotesVOList) {
					for (UserStockPosition i : list) {
						if (stockQuotesVO.getGid() != null && (i.getStockType() + i.getStockCode()).equals(stockQuotesVO.getGid())) {
							BigDecimal buyingShares = new BigDecimal(i.getBuyingShares());//持有股数
							BigDecimal holdingValue = i.getBuyingPrice().multiply(buyingShares);//持有成本=买入价格*持有股数
							BigDecimal marketValue = stockQuotesVO.getNowPrice().multiply(buyingShares);//持有市值=当前价格*持有股数
							if (i.getStockType().equals(StockTypeEnum.US.getCode())) {//美股利率计算
								if (usExchangeRate == null) {
									usExchangeRate = sysParamConfigService.getExchangeRate(CurrencyEnum.USD);//美股利率
								}
								marketValue = marketValue.divide(usExchangeRate, 2, RoundingMode.HALF_UP);//美股持有市值
								holdingValue = holdingValue.divide(usExchangeRate, 2, RoundingMode.HALF_UP);//美股持有成本
							} else {//港股利率计算
								if (hkExchangeRate == null) {
									hkExchangeRate = sysParamConfigService.getExchangeRate(CurrencyEnum.HKD);//港股利率
								}
								marketValue = marketValue.divide(hkExchangeRate, 2, RoundingMode.HALF_UP);//港股持有市值
								holdingValue = holdingValue.divide(hkExchangeRate, 2, RoundingMode.HALF_UP);//港股只有成本
							}
							totalMarketValue = totalMarketValue.add(marketValue);//持仓总市值
							totalHoldingValue = totalHoldingValue.add(holdingValue);//持仓总成本
							if (i.getPositionDirection() == Constant.GO_LONG) {
								totalProfit = totalProfit.add(marketValue.subtract(holdingValue));
								todayProfit = todayProfit.add(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							} else {
								totalProfit = totalProfit.add(holdingValue.subtract(marketValue));
								todayProfit = todayProfit.subtract(stockQuotesVO.getNowPrice().multiply(stockQuotesVO.getPercentageIncrease()));
							}
						}
					}
				}
			}
			totalAssets = totalAssets.add(totalMarketValue);
		}
		vo.setTotalAssets(totalAssets);
		vo.setTotalMarketValue(totalMarketValue);
		vo.setTotalProfit(totalProfit);
		vo.setTodayProfit(todayProfit);
		BigDecimal todayProfitRate = BigDecimal.ZERO;
		try {
			todayProfitRate = todayProfit.divide(totalHoldingValue, 2, RoundingMode.HALF_UP);////当日盈亏/持仓总成本
		} catch (Exception e) {
			todayProfitRate = BigDecimal.ZERO;
		}
		vo.setTodayProfitRate(todayProfitRate);//当日盈亏/持仓总成本
		//增加持仓、挂单、平仓，总数数据
		vo.setUserStockPositionTotal(userStockPositionService.lambdaQuery().eq(UserStockPosition::getUserId, userId).eq(UserStockPosition::getPositionStatus, 2).count());
		vo.setUserStockPendingTotal(userStockPendingService.lambdaQuery().eq(UserStockPending::getUserId, userId).eq(UserStockPending::getPositionStatus, 1).count());
		vo.setUserStockClosingPositionTotal(userStockClosingPositionService.lambdaQuery().eq(UserStockClosingPosition::getUserId, userId).count());
		QueryWrapper<UserStockClosingPosition> qw = new QueryWrapper<>();
		qw.select("ifnull(sum(actual_profit),0) as actual_profit");
		qw.eq("user_id", userId);
		Map<String, Object> map = userStockClosingPositionService.getMap(qw);
		BigDecimal actualProfitSum = (BigDecimal) map.get("actual_profit");
		vo.setAggregateTotalProfit(actualProfitSum.subtract(totalHoldingValue));//平仓总盈亏-持仓总成本
		return Response.successData(vo);
	}
	
	/**
	 * 执行添加用户
	 * @param userInfo
	 * @return
	 */
	@Transactional
	private Response<Void> doSaveUserInfo(UserInfo userInfo,UserDataChangeRecord udcr) {
		//1、存储用户信息
		if (udcr.getDataChangeTypeCode().equals(UserDataChangeTypeEnum.ADD_USER.getCode()) || 
				udcr.getDataChangeTypeCode().equals(UserDataChangeTypeEnum.USER_REGISTER.getCode())) {//新增用户，通用参数处理
			List<LevelDef> levelDefs = levelDefService.lambdaQuery().orderByAsc(LevelDef::getLevelVal).list();
			userInfo.setLevelId(levelDefs.get(0).getId());
			Country country = countryService.getCountryByAreaCode(userInfo.getAreaCode());
			userInfo.setRegion(country.getNameCn());
			userInfo.setPromotionCode(StringUtil.uuid8());
			userInfo.setRegTime(new Date());
		}
		userInfo.insertOrUpdate();
		//用户代理逻辑处理
		if(userInfo.getAgentId() != null && userInfo.getAgentId() > 0) {
			if(userInfo.getAgentId().equals(userInfo.getGeneralAgentId())) {
				AgentMemberInfo um = new AgentMemberInfo();
				um.setAgentId(userInfo.getAgentId());
				um.setMemberUserId(userInfo.getId());
				um.setMemberAgentId(userInfo.getAgentId());
				um.insert();
			} else {
		        List<ChildAndParentVO> uaList = this.pNextAgents(userInfo.getId());
				for(ChildAndParentVO ua : uaList) {
					AgentMemberInfo um = new AgentMemberInfo();
					um.setAgentId(ua.getChildId());
					um.setMemberUserId(userInfo.getId());
					um.setMemberAgentId(userInfo.getAgentId());
					um.insert();
				}
			}
		}
		//2、模拟账户,处理默认属性
		if (udcr.getDataChangeTypeCode().equals(UserDataChangeTypeEnum.ADD_USER.getCode())) {//新增用户，模拟账户处理
			if(userInfo.getAccountType() == Constant.ACCOUNT_TYPE_VISUAL) {
				UserInfo visualUserInfo = new UserInfo();
				visualUserInfo.setId(userInfo.getId());
				visualUserInfo.setRealName("模拟用户" + userInfo.getId());
				visualUserInfo.setCertificateNumber("无");
				visualUserInfo.setRealAuthStatus(UserRealAuthStatusEnum.REVIEWED.getCode());
				visualUserInfo.setFundingStatus(UserFundingStatusEnum.REVIEWED.getCode());
				visualUserInfo.setFollowEnable(true);
				visualUserInfo.updateById();
			}
		}
		//3、用户信息记录
		udcr.setNewContent("登录ID："+userInfo.getId() + udcr.getNewContent());
		udcr.setUserId(userInfo.getId());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.getNameByCode(udcr.getDataChangeTypeCode()));
		udcr.setCreateTime(new Date());
		udcr.insert();
		return Response.success();
	}

	@Override
	public void managerAgentList(Page<AgentListVO> page, AgentListSearchParamVO param) {
		userInfoMapper.managerAgentList(page, param);
	}

	@Override
	public Response<TeamDetailSearchVO> teamDetail(Integer userId) {
		List<TeamMembers> tms = userInfoMapper.teamMembers(userId,null,null);
		if(tms == null || tms.size() == 0) {
			return Response.fail("没有数据");
		}
		TeamMembers tm1 = tms.get(0);
		TeamDetailSearchVO v = new TeamDetailSearchVO();
		v.getMembers().setUserId(userId);
		v.getMembers().setNickname(tm1.getNickname());
		v.getMembers().setLevelVal(tm1.getLevelVal());
		v.getMembers().setLevelName(tm1.getLevelName());
		if(tm1.getMemberUserId() == null) {
			return Response.successData(v);
		}
		findMembers(tms, v.getMembers(), v);
		v.setTotal(v.getTotal() + 1);
		return Response.successData(v);
	}
	
	@Override
	public Response<TeamRebateOrInviteSearchVO> teamRebates(Integer userId) {
		List<TeamMembers> tms = userInfoMapper.teamMembers(userId,null,null);
		if(tms == null || tms.size() == 0) {
			return Response.fail("没有数据");
		}
		TeamMembers tm1 = tms.get(0);
		TeamRebateOrInviteSearchVO v = new TeamRebateOrInviteSearchVO();
		v.setUserId(userId);
		v.setNickname(tm1.getNickname());
		v.setLevelVal(tm1.getLevelVal());
		v.setLevelName(tm1.getLevelName());
		if(tm1.getMemberUserId() == null) {
			return Response.successData(v);
		}
		for(int i = 1; i < tms.size(); i++) {
			TeamMembers tm = tms.get(i);
			if(tm.getMemberLevelVal() < v.getLevelVal()) {
				MemberInfo m = new MemberInfo();
				m.setUserId(tm.getMemberUserId());
				m.setNickname(tm.getMemberNickname());
				m.setLevelVal(tm.getMemberLevelVal());
				m.setLevelName(tm.getMemberLevelName());
				v.setTotal(v.getTotal() + 1);
				switch(tm.getMemberLevelVal()) {
				case 1:
					v.setTotalOfL1(v.getTotalOfL1() + 1);
					v.getMembersOfL1().add(m);
					break;
				case 2:
					v.setTotalOfL2(v.getTotalOfL2() + 1);
					v.getMembersOfL2().add(m);
					break;
				case 3:
					v.setTotalOfL3(v.getTotalOfL3() + 1);
					v.getMembersOfL3().add(m);
					break;
				default:
					v.setTotalOfL4(v.getTotalOfL4() + 1);
					v.getMembersOfL4().add(m);
					break;
				}
			}
		}
		return Response.successData(v);
	}
	
	@Override
	public Response<TeamRebateOrInviteSearchVO> invitedMembers(Integer userId) {
		UserInfo me = this.getById(userId);
		if(me == null) {
			return null;
		}
		LevelDef l = levelDefService.getById(me.getLevelId());
		TeamRebateOrInviteSearchVO v = new TeamRebateOrInviteSearchVO();
		v.setUserId(userId);
		v.setNickname(me.getNickname());
		v.setLevelVal(l.getLevelVal());
		v.setLevelName(l.getLevelName());
		List<UserInfo> members = this.lambdaQuery().eq(UserInfo::getRegInvitationCode, me.getPromotionCode()).list();
		for(UserInfo member : members) {
			MemberInfo m = new MemberInfo();
			m.setUserId(member.getId());
			m.setNickname(member.getNickname());
			l = levelDefService.getById(member.getLevelId());
			m.setLevelVal(l.getLevelVal());
			m.setLevelName(l.getLevelName());
			v.setTotal(v.getTotal() + 1);
			switch(l.getLevelVal()) {
			case 1:
				v.setTotalOfL1(v.getTotalOfL1() + 1);
				v.getMembersOfL1().add(m);
				break;
			case 2:
				v.setTotalOfL2(v.getTotalOfL2() + 1);
				v.getMembersOfL2().add(m);
				break;
			case 3:
				v.setTotalOfL3(v.getTotalOfL3() + 1);
				v.getMembersOfL3().add(m);
				break;
			case 4:
				v.setTotalOfL4(v.getTotalOfL3() + 1);
				v.getMembersOfL4().add(m);
				break;
			default:
				v.setTotalOfL5(v.getTotalOfL4() + 1);
				v.getMembersOfL5().add(m);
				break;
			}
		}
		return Response.successData(v);
	}
	
	private void findMembers(List<TeamMembers> tms, Members members, TeamDetailSearchVO v) {
		for(TeamMembers tm : tms) {
			if(tm.getMemberAgentId().equals(members.getUserId())) {
//				if(compareLevels && tm.getMemberLevelVal() >= members.getLevelVal()) {
//					continue;
//				}
				Members m = new Members();
				m.setUserId(tm.getMemberUserId());
				m.setAgentId(tm.getMemberAgentId());
				m.setNickname(tm.getMemberNickname());
				m.setLevelVal(tm.getMemberLevelVal());
				m.setLevelName(tm.getMemberLevelName());
				members.getMemberList().add(m);
				v.setTotal(v.getTotal() + 1);
				switch(tm.getMemberLevelVal()) {
				case 1:
					v.setTotalOfL1(v.getTotalOfL1() + 1);
					break;
				case 2:
					v.setTotalOfL2(v.getTotalOfL2() + 1);
					break;
				case 3:
					v.setTotalOfL3(v.getTotalOfL3() + 1);
					break;
				default:
					v.setTotalOfL4(v.getTotalOfL4() + 1);
					break;
				}
			}
		}
		for(Members mm : members.getMemberList()) {
			findMembers(tms, mm, v);
		}
	}

	@Override
	@Transactional
	public void addUserRebateDetailOnTransaction(UserInfo fromUser, BigDecimal performance, Integer type, String tradingOrderSn) {
		List<ChildAndParentVO> cap = this.userInfoMapper.pNextAgents(fromUser.getId());
		LevelDef l = this.levelDefService.getById(fromUser.getLevelId());
		for(ChildAndParentVO c : cap) {
			LevelDef al = this.levelDefService.getById(fromUser.getLevelId());
			if(al.getLevelVal() > l.getLevelVal()) {
				UserRebateDetail r = new UserRebateDetail();
				r.setUserId(c.getChildId());
				r.setUserLevelVal(l.getLevelVal());
				r.setUserLevelName(l.getLevelName());
				r.setFromUserId(fromUser.getId());
				r.setFromUserLevelVal(l.getLevelVal());
				r.setFromUserLevelName(l.getLevelName());
				r.setPerformance(performance);
				r.setRebateType(type);
				r.setTradingOrderSn(tradingOrderSn);
				r.insert();
			}
		}
	}

	/**
	 * 数据报表-用户跟投统计
	 */
	@Override
	public void userFollowStatistics(Page<UserFollowStatisticsVO> page, UserFollowStatisticsSearchParamVO param) {
		userInfoMapper.userFollowStatistics(page,param);
	}
}
