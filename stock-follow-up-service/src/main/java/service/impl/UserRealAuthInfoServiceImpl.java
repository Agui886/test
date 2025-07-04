package service.impl;

import entity.IpAddress;
import entity.UserDataChangeRecord;
import entity.UserInfo;
import entity.UserRealAuthInfo;
import entity.common.Response;
import enums.UserDataChangeTypeEnum;
import enums.UserRealAuthStatusEnum;
import mapper.UserRealAuthInfoMapper;
import service.IpAddressService;
import service.UserInfoService;
import service.UserRealAuthInfoService;
import utils.StringUtil;
import vo.server.AddUserRealAuthInfoVO;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户认证信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Service
public class UserRealAuthInfoServiceImpl extends ServiceImpl<UserRealAuthInfoMapper, UserRealAuthInfo> implements UserRealAuthInfoService {

	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	/**
	 * 管理系统->用户管理->认证审核->修改认证状态
	 */
	@Override
	@Transactional
	public Response<Void> managerUpdateRealAuthStatus(Integer id, Integer realAuthStatus, String ip, String operator) {
		if(realAuthStatus != UserRealAuthStatusEnum.REVIEWED.getCode() && realAuthStatus != UserRealAuthStatusEnum.FAILED.getCode()) {
			return Response.fail("状态参数错误");
		}
		UserRealAuthInfo ura = this.getById(id);
		if(ura == null) {
			return Response.fail("记录不存在");
		}
		UserInfo user = userInfoService.getById(ura.getUserId());
		if(user == null) {
			return Response.fail("用户信息错误");
		}
		if(user.getRealAuthStatus() != UserRealAuthStatusEnum.APPLYING.getCode()) {
			return Response.fail("用户认证状态不是审核中，修改失败");
		}
		if(realAuthStatus == UserRealAuthStatusEnum.REVIEWED.getCode()) {
			userInfoService.lambdaUpdate()
			.eq(UserInfo::getId, user.getId())
			.set(UserInfo::getRealAuthStatus, realAuthStatus)
			.set(UserInfo::getFollowEnable, true)
			.set(ura.getCertificateType() == 1, UserInfo :: getCertificateType,ura.getCertificateType())//是护照需要修改，证件类型
			.set(ura.getCertificateType() == 1, UserInfo :: getCertificateNumber,ura.getCertificateNumber())//是护照需要修改，证件号
			.set(ura.getCertificateType() == 1, UserInfo :: getRealName,ura.getRealName())//是护照需要修改，真实姓名
			.update();
		} else {
			userInfoService.lambdaUpdate().eq(UserInfo::getId, user.getId()).set(UserInfo::getRealAuthStatus, realAuthStatus).update();
		}
		Date now = new Date();
		this.lambdaUpdate()
			.set(UserRealAuthInfo::getRealAuthStatus, realAuthStatus)
			.set(UserRealAuthInfo::getOperateTime, now)
			.set(UserRealAuthInfo::getOperator, operator)
			.eq(UserRealAuthInfo::getId, id).update();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(user.getId());
		udcr.setCreateTime(now);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.REAL_AUTH_STATUS.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.REAL_AUTH_STATUS.getName());
		udcr.setOldContent(UserRealAuthStatusEnum.getNameByCode(user.getRealAuthStatus()));
		udcr.setNewContent(UserRealAuthStatusEnum.getNameByCode(realAuthStatus));
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.insert();
		return Response.success();
	}

	/**
	 * 提交实名认证申请
	 */
	@Override
	@Transactional
	public Response<Void> submitRealAuthInfo(Integer userId, AddUserRealAuthInfoVO param, String ip) {
		UserInfo ui = userInfoService.getById(userId);
		if(ui.getRealAuthStatus() == UserRealAuthStatusEnum.APPLYING.getCode() || ui.getRealAuthStatus() == UserRealAuthStatusEnum.REVIEWED.getCode()) {
			return Response.fail("提交失败，您已经提交过实名认证申请");
		}
		if(StringUtil.isEmpty(param.getRegion())) {
			return Response.fail("请选择国籍");
		}
		if(param.getCertificateType() == null) {
			return Response.fail("请选择证件类型");
		}
		if (param.getCertificateType() == 0) {
			if (StringUtil.isEmpty(param.getIdFrontImg())) {
				return Response.fail("请选择身份证正面照片");
			}
			if (StringUtil.isEmpty(param.getIdBackImg())) {
				return Response.fail("请选择身份证背面照片");
			}
			if (StringUtil.isEmpty(param.getIdHandImg())) {
				return Response.fail("请选择身份证手持照片");
			}
			if (!ui.isFaceVerify()) {
				return Response.fail("用户未通过人脸认证");
			}
			//自动设置通过人脸认证的，真实姓名、真实证件号
			param.setRealName(ui.getRealName());
			param.setCertificateNumber(ui.getCertificateNumber());
		} else {
			if(StringUtil.isEmpty(param.getRealName())) {
				return Response.fail("请输入姓名");
			}
			if(StringUtil.isEmpty(param.getCertificateNumber())) {
				return Response.fail("请输入证件号码");
			}
			if(StringUtil.isEmpty(param.getPassportDataImg())) {
				return Response.fail("请选择护照照片");
			}
			if(StringUtil.isEmpty(param.getEnLastName())) {
				return Response.fail("请输入英文姓氏");
			}
			if(StringUtil.isEmpty(param.getEnFirstName())) {
				return Response.fail("请输入英文姓名");
			}
			if(param.getGender() == null || param.getGender() != 1 && param.getGender() != 0) {
				return Response.fail("请选择性别");
			}
			if(StringUtil.isEmpty(param.getTaxFilingArea())) {
				return Response.fail("请输入报税地区");
			}
			if(param.getBirthday() == null) {
				return Response.fail("请选择日期");
			}
			if(param.getCertificateValidityPeriod() == null) {
				return Response.fail("请选择证件有效期截止日期");
			}
		}
		if(StringUtil.isEmpty(param.getSignature())) {
			return Response.fail("请上传签名");
		}
		Date now = new Date();
		UserInfo updateRealAuthStatus = new UserInfo();
		updateRealAuthStatus.setId(userId);
		updateRealAuthStatus.setRealAuthStatus(UserRealAuthStatusEnum.APPLYING.getCode());
		updateRealAuthStatus.updateById();
		UserRealAuthInfo urai = new UserRealAuthInfo();
		urai.setUserId(userId);
		urai.setCertificateType(param.getCertificateType());
		urai.setCertificateNumber(param.getCertificateNumber());
		urai.setRealName(param.getRealName());
		urai.setGender(param.getGender());
		urai.setIdFrontImg(param.getIdFrontImg());
		urai.setIdBackImg(param.getIdBackImg());
		urai.setIdHandImg(param.getIdHandImg());
		urai.setPassportDataImg(param.getPassportDataImg());
		urai.setEnLastName(param.getEnLastName());
		urai.setEnFirstName(param.getEnFirstName());
		urai.setEnMiddleName(param.getEnMiddleName());
		urai.setRegion(param.getRegion());
		urai.setBirthday(param.getBirthday());
		urai.setCertificateValidityPeriod(param.getCertificateValidityPeriod());
		urai.setRequestTime(now);
		urai.setRegTime(ui.getRegTime());
		urai.setRegIp(ui.getRegIp());
		urai.setRegAddress(ui.getRegAddress());
		urai.setTaxFilingArea(param.getTaxFilingArea());
		urai.setSignature(param.getSignature());
		urai.insert();
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(ui.getId());
		udcr.setCreateTime(now);
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.REAL_AUTH_STATUS.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.REAL_AUTH_STATUS.getName());
		udcr.setOldContent(UserRealAuthStatusEnum.NOT.getName());
		udcr.setNewContent(UserRealAuthStatusEnum.APPLYING.getName());
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(ui.getOperator());
		udcr.insert();
		return Response.success();
	}

}
