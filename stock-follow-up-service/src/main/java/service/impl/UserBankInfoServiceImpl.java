package service.impl;

import entity.IpAddress;
import entity.UserBankInfo;
import entity.UserDataChangeRecord;
import entity.UserInfo;
import entity.common.Response;
import enums.UserDataChangeTypeEnum;
import mapper.UserBankInfoMapper;
import service.IpAddressService;
import service.UserBankInfoService;
import service.UserDataChangeRecordService;
import service.UserInfoService;
import utils.StringUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 会员银行卡信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Service
public class UserBankInfoServiceImpl extends ServiceImpl<UserBankInfoMapper, UserBankInfo> implements UserBankInfoService {
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private UserDataChangeRecordService userDataChangeRecordService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Override
	@Transactional
	public Response<Void> add(Integer userId, UserBankInfo userBankInfo, String ip, String operator) {
		if(StringUtil.isEmpty(userBankInfo.getBankCode()) || StringUtil.isEmpty(userBankInfo.getBankName())) {
			return Response.fail("请选择银行");
		}
		if(StringUtil.isEmpty(userBankInfo.getProvince())) {
			return Response.fail("请输入省份");
		}
		if(StringUtil.isEmpty(userBankInfo.getCity())) {
			return Response.fail("请输入城市");
		}
		if(StringUtil.isEmpty(userBankInfo.getBranchBankAddress())) {
			return Response.fail("请输入支行地址");
		}
		if(StringUtil.isEmpty(userBankInfo.getRealName())) {
			return Response.fail("请输入账户姓名");
		}
		if(StringUtil.isEmpty(userBankInfo.getCardNo())) {
			return Response.fail("请输入银行卡号");
		}
		UserInfo user = userInfoService.getById(userId);
		if(user == null) {
			return Response.fail("用户信息错误");
		}
		userBankInfo.setUserId(userId);
		String oldContent = "";
		if(userBankInfo.getId() == null || userBankInfo.getId() == 0) {
			if(this.lambdaQuery().eq(UserBankInfo::getUserId, user.getId()).count() > 0) {
				return Response.fail("您已绑定银行卡信息");
			}
			userBankInfo.setId(null);
			userBankInfo.insert();
		} else {
			oldContent = this.getById(userBankInfo.getId()).toString();
			userBankInfo.updateById();
		}
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(user.getId());
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.BANK.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.BANK.getName());
		udcr.setNewContent(userBankInfo.toString());
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.setOldContent(oldContent);
		udcr.setNewContent(userBankInfo.toString());
		udcr.insert();
		return Response.success();
	}

	
	@Override
	@Transactional
	public Response<Void> edit(UserBankInfo userBankInfo, String ip, String operator) {
		UserInfo user = userInfoService.getById(userBankInfo.getUserId());
		if(user == null) {
			return Response.fail("用户信息错误");
		}
		if(StringUtil.isEmpty(userBankInfo.getBankCode()) || StringUtil.isEmpty(userBankInfo.getBankName())) {
			return Response.fail("请选择银行");
		}
		if(StringUtil.isEmpty(userBankInfo.getProvince())) {
			return Response.fail("请输入省份");
		}
		if(StringUtil.isEmpty(userBankInfo.getCity())) {
			return Response.fail("请输入城市");
		}
		if(StringUtil.isEmpty(userBankInfo.getBranchBankAddress())) {
			return Response.fail("请输入支行地址");
		}
		if(StringUtil.isEmpty(userBankInfo.getRealName())) {
			return Response.fail("请输入账户姓名");
		}
		if(StringUtil.isEmpty(userBankInfo.getCardNo())) {
			return Response.fail("请输入银行卡号");
		}
		UserDataChangeRecord udcr = new UserDataChangeRecord();
		udcr.setUserId(user.getId());
		udcr.setDataChangeTypeCode(UserDataChangeTypeEnum.BANK.getCode());
		udcr.setDataChangeTypeName(UserDataChangeTypeEnum.BANK.getName());
		udcr.setIp(ip);
		IpAddress ia = ipAddressService.getIpAddress(ip);
		udcr.setIpAddress(ia.getAddress2());
		udcr.setOperator(operator);
		udcr.setNewContent(userBankInfo.toString());
		if(userBankInfo != null) {
			if(userBankInfo.getId() == 0) {
				userBankInfo.setId(null);
			} else {
				UserBankInfo old = this.getById(userBankInfo.getId());
				if(old == null || !old.getUserId().equals(user.getId())) {
					return Response.fail("银行卡信息错误");
				}
				if(old.equals(userBankInfo)) {
					return Response.success();
				}
				if(this.lambdaQuery().eq(UserBankInfo::getCardNo, userBankInfo.getCardNo()).ne(UserBankInfo::getUserId, user.getId()).count() > 0) {
					return Response.fail("此卡号已经绑定其它用户，请重新输入");
				}
				udcr.setOldContent(old.toString());
			}
		}
		userBankInfo.insertOrUpdate();
		udcr.insert();
		return Response.success();
	}
}
