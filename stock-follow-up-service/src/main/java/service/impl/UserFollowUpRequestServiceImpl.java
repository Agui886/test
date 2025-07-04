package service.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import entity.SiteInternalMessage;
import entity.TutorInfo;
import entity.UserFollowUpRequest;
import entity.UserInfo;
import entity.TutorInfo.Configuration;
import entity.UserFollowUpRecord;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import enums.FollowConfigurationItemTypeEnum;
import enums.UserFollowRequestStatusEnum;
import enums.UserFollowStatusEnum;
import mapper.UserFollowUpRecordMapper;
import mapper.UserFollowUpRequestMapper;
import service.IpAddressService;
import service.SysParamConfigService;
import service.TutorInfoService;
import service.UserFollowUpRecordService;
import service.UserFollowUpRequestService;
import service.UserInfoService;
import utils.OrderNumberGenerator;
import utils.StringUtil;
import vo.manager.UserFollowUpRequestListSearchParamVO;
import vo.server.ApplyFollowRequestVO;
import vo.server.UserFollowRequestOrRecordParamVO;
import vo.server.UserFollowRequestOrRecordVO;


/**
 * <p>
 * 用户跟投申请表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Service
public class UserFollowUpRequestServiceImpl extends ServiceImpl<UserFollowUpRequestMapper, UserFollowUpRequest> implements UserFollowUpRequestService {


	@Resource
	private UserFollowUpRequestMapper userFollowUpRequestMapper;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@Resource
	private UserFollowUpRecordService userFollowUpRecordService;
	
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	/**
	 * 跟投管理-用户跟投审核
	 */
	@Override
	public void managerList(Page<UserFollowUpRequest> page, UserFollowUpRequestListSearchParamVO param) {
		userFollowUpRequestMapper.managerList(page, param);
	}

	/**
	 * 跟投-用户跟投记录（审核跟投记录/跟投记录）
	 */
	@Override
	public Response<Page<UserFollowRequestOrRecordVO>> userFollowRequestOrRecord(Page<UserFollowRequestOrRecordVO> page,
			UserFollowRequestOrRecordParamVO param) {
		userFollowUpRequestMapper.userFollowRequestOrRecord(page,param);
		return Response.successData(page);
	}

	/**
	 * 跟投-导师信息-跟单详情-申请跟投
	 */
	@Override
	@Transactional
	public Response<Void> applyFollowRequest(ApplyFollowRequestVO param) {
		//1、获取用户对象
		UserInfo userInfo = userInfoService.getById(param.getUserId());
		if (userInfo != null ) {
			//2、申请跟投金额<用户可用余额
			if (param.getAmount().compareTo(userInfo.getAvailableAmt()) == -1) {
				if(param.getAmount().remainder(new BigDecimal("100")).compareTo(BigDecimal.ZERO) == 0) {
					//3、获取导师信息
					TutorInfo tutorInfo = tutorInfoService.getById(param.getTutorId());
					if (tutorInfo != null) {
						
						/**此处不需要验证了**/
//						//3.1、私域，验证邀请码
//						if(tutorInfo.getDomainType() == TutorInfo.DOMAIN_PRIVATE) {
//							//3.2、判断导师邀请码与输入邀请码是否一致
//							if (!tutorInfo.getPrivateInvitationCode().equals(param.getPrivateInvitationCode())) {
//								return Response.fail("导师邀请码不正确");
//							}
//						}
						/**此处不需要验证了**/
						
						//4、获取导师项目
						List<Configuration> configurationList = tutorInfo.getConfiguration();
						Boolean tutorConfiguration = false;//默认导师项目未开启
						for(Configuration configuration : configurationList) {
							if (configuration.getIsEnabled() && configuration.getItemType() == param.getItemType()) {//申请项目与导师项目一致，并且项目状态是开启
								//4.1、验证-信号价格区间
								if (configuration.getMinAmount().compareTo(configuration.getMaxAmount()) >= 0) {
									return Response.fail("项目跟投金额，区间不符");
								}
								//4.2、验证-交易金额
								if (param.getAmount().compareTo(configuration.getMinAmount()) < 0 || param.getAmount().compareTo(configuration.getMaxAmount()) > 0) {
									return Response.fail("申请跟投金额，不在项目跟投金额区间内");
								} 
								tutorConfiguration = true;//设置导师项目已开启
							}
						}
						//5、判断导师项目是否开始
						if (tutorConfiguration) {
							//5.1、判断跟投审核表，
							int followRequestCount = this.lambdaQuery().
									eq(UserFollowUpRequest::getUserId, param.getUserId()).//当前用户
									eq(UserFollowUpRequest::getTutorId, param.getTutorId()).//选择导师
									eq(UserFollowUpRequest::getItemType, param.getItemType()).//选择项目
									eq(UserFollowUpRequest::getStatus, UserFollowRequestStatusEnum.AUDIT.getCode()).//审核状态
									count();
							//5.2、存在跟投审核数据，不允许申请
							if (followRequestCount > 0) {
								return Response.fail("您已申请过跟投审核记录");	
							}
							//5.3、判断跟投记录表，
							int followRecordCount = userFollowUpRecordService.lambdaQuery().
									eq(UserFollowUpRecord::getUserId, param.getUserId()).//当前用户
									eq(UserFollowUpRecord::getTutorId, param.getTutorId()).//选择导师
									eq(UserFollowUpRecord::getItemType, param.getItemType()).//选择项目
									ne(UserFollowUpRecord::getFollowStatus, UserFollowStatusEnum.FINISH.getCode()).//不查拒绝
									count();	
							//5.4、存在跟投记录数据，不允许申请
							if (followRecordCount > 0) {
								return Response.fail("您正在跟投中");	
							}
							//6、处理数据存储相关逻辑,增加跟投审核记录
							UserFollowUpRequest userFollowUpRequest = new UserFollowUpRequest();
							userFollowUpRequest.setUserId(param.getUserId());
							userFollowUpRequest.setTutorId(param.getTutorId());
							userFollowUpRequest.setItemType(param.getItemType());
							userFollowUpRequest.setAmount(param.getAmount());
							userFollowUpRequest.setRequestTime(new Date());
							userFollowUpRequest.setStatus(UserFollowRequestStatusEnum.AUDIT.getCode());
							userFollowUpRequest.setOperator(userInfo.getOperator());
							userFollowUpRequest.setFollowRequestOrderSn(OrderNumberGenerator.create(9));
							this.save(userFollowUpRequest);
							//7、扣除用户可用金额
							userInfoService.updateUserAvailableAmt(param.getUserId(), 
									AmtDeTypeEnum.ApplyFollowRequest, param.getAmount(),
									"客户端，申请跟投",
									CurrencyEnum.CNY,
									BigDecimal.ONE,
									param.getIp(),
									param.getIpAddress(),
									param.getOperator(),
									userFollowUpRequest.getFollowRequestOrderSn()
									);
						}else {
							return Response.fail("导师项目未开启");	
						}
						
					}else {
						return Response.fail("导师不存在");	
					}
				}else {
					return Response.fail("申请跟投金额,必须是整百");
				}
			}else {
				return Response.fail("用户可用余额不够");
			}
		}else {
			return Response.fail("用户不存在");
		}
		return Response.success();
	}

	/**
	 * 跟投管理-用户跟投审核-状态修改
	 */
	@Override
	@Transactional
	public Response<Void> updateStatus(Integer id, Integer status,String ip, String operator) {
		if(status != UserFollowRequestStatusEnum.PASS.getCode() && status != UserFollowRequestStatusEnum.REFUSE.getCode()) {
			return Response.fail("状态参数错误");
		}
		UserFollowUpRequest ufr = this.getById(id);
		if(ufr == null) {
			return Response.fail("记录不存在");
		}
		if(ufr.getStatus() != UserFollowRequestStatusEnum.AUDIT.getCode()) {
			return Response.fail("操作失败，记录状态不是审核中");
		}
		//2、修改审核状态
		this.lambdaUpdate()
			.set(UserFollowUpRequest::getStatus, status)
			.set(UserFollowUpRequest::getOperator, operator)
			.set(UserFollowUpRequest::getOperateTime, new Date()).eq(UserFollowUpRequest::getId, id).update();
		//3、通过审核，生成跟投记录
		if (status == UserFollowRequestStatusEnum.PASS.getCode()) {
			//获取系统配置
			StockParamConfig stockParamConfig = sysParamConfigService.getSysParamConfig();
			// 将startDateTime解析为当天的,开始跟投时间节点-限制
	        DateTime startDateTime = DateUtil.parseTimeToday(stockParamConfig.getFollowStartTimeRestrict());
	        // 获取当前时间
	        DateTime now = DateUtil.date();
	        //开始跟投时间
	        DateTime followStartTime;
	        // 逻辑判断
	        if (now.isBefore(startDateTime)) {//当前时间早于设定时间 → 取当前时间
	        	followStartTime = now;//开始跟投时间=当前时间
	        } else {//当前时间等于或晚于设定时间 → 取次日0点
	        	followStartTime = DateUtil.beginOfDay(DateUtil.tomorrow());//开始跟投时间=次日0点
	        }
	        //结束跟投时间=开始跟投延后的天数，延后的天数，根据项目类型，获取延后天数值
	        DateTime followEndTime = DateUtil.offsetDay(followStartTime, FollowConfigurationItemTypeEnum.getVlueByCode(ufr.getItemType()));
			//设置跟投记录属性
			UserFollowUpRecord userFollowUpRecord = new UserFollowUpRecord();
			userFollowUpRecord.setUserId(ufr.getUserId());
			userFollowUpRecord.setTutorId(ufr.getTutorId());
			userFollowUpRecord.setItemType(ufr.getItemType());
			userFollowUpRecord.setFollowStartTime(followStartTime);
			userFollowUpRecord.setFollowStatus(UserFollowStatusEnum.UNDERWAY.getCode());
			userFollowUpRecord.setFollowEndTime(followEndTime);
			userFollowUpRecord.setInitialFollowSum(ufr.getAmount());
			userFollowUpRecord.setCurrentFollowSum(userFollowUpRecord.getInitialFollowSum());
			userFollowUpRecord.setFollowRequestId(id);
			userFollowUpRecord.setFollowOrderSn(OrderNumberGenerator.create(7));
			//添加跟投记录
			userFollowUpRecord.insert();
		}else if (status == UserFollowRequestStatusEnum.REFUSE.getCode()) {//拒绝审核退钱
			userInfoService.updateUserAvailableAmt(ufr.getUserId(), 
					AmtDeTypeEnum.RefuseFollowRequest, ufr.getAmount(),
					"后台，拒绝跟投审核",
					CurrencyEnum.CNY,
					BigDecimal.ONE,
					ip,
					ipAddressService.getIpAddress(ip).getAddress2(),
					operator,
					ufr.getFollowRequestOrderSn()
					);
		}
		return Response.success();
	}

}
