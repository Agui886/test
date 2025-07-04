package service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import entity.TutorInfo;
import entity.TutorInfo.Configuration;
import entity.TutorInfoChangeRecord;
import entity.common.Response;
import enums.TutorInfoChangeTypeEnum;
import mapper.TutorInfoMapper;
import service.IpAddressService;
import service.TutorInfoChangeRecordService;
import service.TutorInfoService;
import utils.StringUtil;
import vo.manager.TutorChangeRecordListSearchParamVO;
import vo.manager.TutorDataStatisticsSearchParamVO;
import vo.manager.TutorDataStatisticsVO;
import vo.server.FollowTutorListParamVO;
import vo.server.FollowTutorListVO;

/**
 * <p>
 * 导师信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Service
public class TutorInfoServiceImpl extends ServiceImpl<TutorInfoMapper, TutorInfo> implements TutorInfoService {
	
	@Resource
	private IpAddressService ipAddressService;
	
	@Resource
	private TutorInfoMapper tutorInfoMapper;
	
	@Resource
	private TutorInfoChangeRecordService tutorInfoChangeRecordService;
	
	@Override
	@Transactional
	public Response<Void> add(TutorInfo tutorInfo, String ip, String operator) {
		if(tutorInfo == null) {
			return Response.fail("请填写导师信息");
		}
		if(StringUtil.isEmpty(tutorInfo.getAvatarPic())) {
			return Response.fail("请上传导师头像");
		}
		if(StringUtil.isEmpty(tutorInfo.getTutorName())) {
			return Response.fail("请输入导师名称");
		}
		if(StringUtil.isEmpty(tutorInfo.getTutorProfile())) {
			return Response.fail("请输入导师简介");
		}
		if(tutorInfo.getDomainType() == null) {
			return Response.fail("请选择公/私域");
		} else if(tutorInfo.getDomainType() == TutorInfo.DOMAIN_PRIVATE && StringUtil.isEmpty(tutorInfo.getPrivateInvitationCode())) {
			return Response.fail("请输入私域邀请码");
		}
		if(tutorInfo.getExperience() == null) {
			return Response.fail("请输入从业年限");
		}
		if(StringUtil.isEmpty(tutorInfo.getLabel())) {
			return Response.fail("请输入导师标签");
		}
		List<Configuration> configuration = tutorInfo.getConfiguration();
		if(configuration == null || configuration.size() == 0) {
			return Response.fail("请至少添加一项项目配置");
		}
		boolean isEnabled = false;
		BigDecimal ZERO = BigDecimal.ZERO;
		for(Configuration i : configuration) {
			if(i.getIsEnabled()) {
				isEnabled = true;
				String itemName;
				switch(i.getItemType()) {
				default:
					itemName = "每日跟投";
					break;
				case 1:
					itemName = "7日跟投";
					break;
				case 2:
					itemName = "16日跟投";
					break;
				case 3:
					itemName = "38日跟投";
					break;
				case 4:
					itemName = "108日跟投";
					break;
				case 5:
					itemName = "180日跟投";
					break;
				case 6:
					itemName = "360日跟投";
					break;
				}
				if(i.getMinAmount() == null || i.getMinAmount().compareTo(ZERO) <= 0) {
					return Response.fail("请输入" + itemName + "最小跟投金额");
				}
				if(i.getMaxAmount() == null || i.getMaxAmount().compareTo(ZERO) <= 0) {
					return Response.fail("请输入" + itemName + "最大跟投金额");
				}
				if(i.getMaxAmount().compareTo(i.getMinAmount()) == -1) {
					return Response.fail(itemName + "最大跟投金额不能小于最小跟投金额");
				}
				if(i.getTutorCommissionRatio() == null || i.getTutorCommissionRatio().compareTo(ZERO) < 0) {
					return Response.fail("请输入" + itemName + "导师抽佣比例");
				}
				if(i.getPlatformCommissionRatio() == null || i.getPlatformCommissionRatio().compareTo(ZERO) < 0) {
					return Response.fail("请输入" + itemName + "平台抽佣比例");
				}
			}
		}
		tutorInfo.setId(null);
		tutorInfo.setIsEnabled(isEnabled);
		tutorInfo.insert();
		TutorInfoChangeTypeEnum e = TutorInfoChangeTypeEnum.CREATE;
		TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
		tr.setTutorId(tutorInfo.getId());
		tr.setDataChangeTypeCode(e.getCode());
		tr.setDataChangeTypeName(e.getName());
		tr.setNewContent("创建导师成功，导师id：" + tutorInfo.getId());
		tr.setOperator(operator);
		tr.setIp(ip);
		tr.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		tr.insert();
		return Response.success();
	}
	
	@Override
	@Transactional
	public Response<Void> edit(TutorInfo tutorInfo, String ip, String operator) {
		if(tutorInfo == null) {
			return Response.fail("请填写导师信息");
		}
		TutorInfo old = this.getById(tutorInfo.getId());
		if(old == null) {
			return Response.fail("导师信息不存在");
		}
		List<TutorInfoChangeRecord> ticrLst = new ArrayList<>();
		LambdaUpdateWrapper<TutorInfo> luw = new LambdaUpdateWrapper<>();
		Date now = new Date();
		String address = ipAddressService.getIpAddress(ip).getAddress2();
		luw.eq(TutorInfo::getId, old.getId());
		if(StringUtil.isEmpty(tutorInfo.getAvatarPic())) {
			return Response.fail("请上传导师头像");
		}
		if(!old.getAvatarPic().equals(tutorInfo.getAvatarPic())) {
			luw.set(TutorInfo::getAvatarPic, tutorInfo.getAvatarPic());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.AVATAR.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.AVATAR.getName());
			tr.setOldContent(old.getAvatarPic());
			tr.setNewContent(tutorInfo.getAvatarPic());
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		if(StringUtil.isEmpty(tutorInfo.getTutorName())) {
			return Response.fail("请输入导师名称");
		}
		if(!old.getTutorName().equals(tutorInfo.getTutorName())) {
			luw.set(TutorInfo::getTutorName, tutorInfo.getTutorName());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.NAME.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.NAME.getName());
			tr.setOldContent(old.getTutorName());
			tr.setNewContent(tutorInfo.getTutorName());
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		if(StringUtil.isEmpty(tutorInfo.getTutorProfile())) {
			return Response.fail("请输入导师简介");
		}
		if(!old.getTutorProfile().equals(tutorInfo.getTutorProfile())) {
			luw.set(TutorInfo::getTutorProfile, tutorInfo.getTutorProfile());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.PROFILE.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.PROFILE.getName());
			tr.setOldContent(old.getTutorProfile());
			tr.setNewContent(tutorInfo.getTutorProfile());
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		if(tutorInfo.getDomainType() == null) {
			return Response.fail("请选择公/私域");
		} else if(tutorInfo.getDomainType() == TutorInfo.DOMAIN_PRIVATE && StringUtil.isEmpty(tutorInfo.getPrivateInvitationCode())) {
			return Response.fail("请输入私域邀请码");
		}
		if(tutorInfo.getDomainType() != old.getDomainType()) {
			luw.set(TutorInfo::getDomainType, tutorInfo.getDomainType());
			luw.set(TutorInfo::getPrivateInvitationCode, tutorInfo.getPrivateInvitationCode());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.DOMAIN.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.DOMAIN.getName());
			if(old.getDomainType().equals(0)) {
				tr.setOldContent("公域");
				tr.setNewContent("私域\n私域邀请码：" + tutorInfo.getPrivateInvitationCode());
			} else {
				tr.setOldContent("私域\n私域邀请码：" + tutorInfo.getPrivateInvitationCode());
				tr.setNewContent("公域");
			}
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		} else if(!old.getPrivateInvitationCode().equals(tutorInfo.getPrivateInvitationCode())) {
			luw.set(TutorInfo::getPrivateInvitationCode, tutorInfo.getPrivateInvitationCode());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.DOMAIN.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.DOMAIN.getName());
			tr.setOldContent("私域邀请码：" + old.getPrivateInvitationCode());
			tr.setNewContent("私域邀请码：" + tutorInfo.getPrivateInvitationCode());
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		if (tutorInfo.getSort() != null) {
			if(!old.getSort().equals(tutorInfo.getSort())) {
				luw.set(TutorInfo::getSort, tutorInfo.getSort());
				TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
				tr.setTutorId(tutorInfo.getId());
				tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.EXPERIENCE.getCode());
				tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.EXPERIENCE.getName());
				tr.setOldContent("排序："+old.getSort());
				tr.setNewContent("排序："+tutorInfo.getSort());
				tr.setOperator(operator);
				tr.setIp(ip);
				tr.setIpAddress(address);
				tr.setCreateTime(now);
				ticrLst.add(tr);
			}
		}
		if(tutorInfo.getExperience() == null) {
			return Response.fail("请输入从业年限");
		}
		if(!old.getExperience().equals(tutorInfo.getExperience())) {
			luw.set(TutorInfo::getExperience, tutorInfo.getExperience());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.EXPERIENCE.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.EXPERIENCE.getName());
			tr.setOldContent(old.getExperience() + "年");
			tr.setNewContent(tutorInfo.getExperience() + "年");
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		if(StringUtil.isEmpty(tutorInfo.getLabel())) {
			return Response.fail("请输入导师标签");
		}
		if(!old.getLabel().equals(tutorInfo.getLabel())) {
			luw.set(TutorInfo::getLabel, tutorInfo.getLabel());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.LABEL.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.LABEL.getName());
			tr.setOldContent(old.getLabel());
			tr.setNewContent(tutorInfo.getLabel());
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		List<Configuration> configuration = tutorInfo.getConfiguration();
		if(configuration == null || configuration.size() == 0) {
			return Response.fail("请至少添加一项项目配置");
		}
		if(!old.getConfigurationJson().equals(tutorInfo.getConfigurationJson())) {
			boolean isEnabled = false;
			BigDecimal ZERO = BigDecimal.ZERO;
			for(Configuration i : configuration) {
				if(i.getIsEnabled()) {
					isEnabled = true;
					String itemName;
					switch(i.getItemType()) {
					default:
						itemName = "每日跟投";
						break;
					case 1:
						itemName = "7日跟投";
						break;
					case 2:
						itemName = "16日跟投";
						break;
					case 3:
						itemName = "38日跟投";
						break;
					case 4:
						itemName = "108日跟投";
						break;
					case 5:
						itemName = "180日跟投";
						break;
					case 6:
						itemName = "360日跟投";
						break;
					}
					if(i.getMinAmount() == null || i.getMinAmount().compareTo(ZERO) <= 0) {
						return Response.fail("请输入" + itemName + "最小跟投金额");
					}
					if(i.getMaxAmount() == null || i.getMaxAmount().compareTo(ZERO) <= 0) {
						return Response.fail("请输入" + itemName + "最大跟投金额");
					}
					if(i.getMaxAmount().compareTo(i.getMinAmount()) == -1) {
						return Response.fail(itemName + "最大跟投金额不能小于最小跟投金额");
					}
					if(i.getTutorCommissionRatio() == null || i.getTutorCommissionRatio().compareTo(ZERO) < 0) {
						return Response.fail("请输入" + itemName + "导师抽佣比例");
					}
					if(i.getPlatformCommissionRatio() == null || i.getPlatformCommissionRatio().compareTo(ZERO) < 0) {
						return Response.fail("请输入" + itemName + "平台抽佣比例");
					}
				}
			} 
			if(!isEnabled && old.getIsEnabled()) {
				luw.set(TutorInfo::getIsEnabled, false);
				TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
				tr.setTutorId(tutorInfo.getId());
				tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.STATUS.getCode());
				tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.STATUS.getName());
				tr.setOldContent("可跟股");
				tr.setNewContent("禁止跟投");
				tr.setOperator(operator);
				tr.setIp(ip);
				tr.setIpAddress(address);
				tr.setCreateTime(now);
				ticrLst.add(tr);
			}
			luw.set(TutorInfo::getConfigurationJson, tutorInfo.getConfigurationJson());
			TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
			tr.setTutorId(tutorInfo.getId());
			tr.setDataChangeTypeCode(TutorInfoChangeTypeEnum.CONFIGURATION.getCode());
			tr.setDataChangeTypeName(TutorInfoChangeTypeEnum.CONFIGURATION.getName());
			tr.setOldContent(old.getConfigurationContent());
			tr.setNewContent(tutorInfo.getConfigurationContent());
			tr.setOperator(operator);
			tr.setIp(ip);
			tr.setIpAddress(address);
			tr.setCreateTime(now);
			ticrLst.add(tr);
		}
		if(ticrLst.size() > 0) {
			this.update(luw);
			tutorInfoChangeRecordService.saveBatch(ticrLst);
		}
		return Response.success();
	}
	
	public Response<Void> delete(Integer id, String ip, String operator) {
		TutorInfo ti = this.getById(id);
		if(ti == null) {
			return Response.fail("导师信息不存在");
		}
		ti.deleteById();
		TutorInfoChangeTypeEnum e = TutorInfoChangeTypeEnum.DELETE;
		TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
		tr.setTutorId(id);
		tr.setDataChangeTypeCode(e.getCode());
		tr.setDataChangeTypeName(e.getName());
		tr.setOldContent("导师id：" + ti.getId());
		tr.setNewContent("删除导师成功");
		tr.setOperator(operator);
		tr.setIp(ip);
		tr.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		tr.insert();
		return Response.success();
	}
	
	@Override
	@Transactional
	public Response<Void> updateEnableStatus(Integer id, Boolean status, String ip, String operator) {
		TutorInfo ti = this.getById(id);
		if(ti == null) {
			return Response.fail("导师信息不存在");
		}
		if(ti.getIsEnabled() == status) {
			return Response.fail("状态已修改，请勿重复操作");
		}
		this.lambdaUpdate().set(TutorInfo::getIsEnabled, status).eq(TutorInfo::getId, id).update();
		TutorInfoChangeTypeEnum e = TutorInfoChangeTypeEnum.STATUS;
		TutorInfoChangeRecord tr = new TutorInfoChangeRecord();
		tr.setTutorId(id);
		tr.setDataChangeTypeCode(e.getCode());
		tr.setDataChangeTypeName(e.getName());
		String oldContent, newContent;
		if(ti.getIsEnabled()) {
			oldContent = "可跟投";
			newContent = "禁止跟投";
		} else {
			oldContent = "禁止跟投";
			newContent = "可跟投";
		}
		tr.setOldContent(oldContent);
		tr.setNewContent(newContent);
		tr.setOperator(operator);
		tr.setIp(ip);
		tr.setIpAddress(ipAddressService.getIpAddress(ip).getAddress2());
		tr.insert();
		return Response.success();
	}

	/**
	 * 客户端-跟投-导师列表
	 */
	@Override
	public void followTutorList(Page<FollowTutorListVO> page, FollowTutorListParamVO param){
		tutorInfoMapper.followTutorList(page,param);
	}

	/**
	 * 数据报表-导师数据统计
	 */
	@Override
	public void tutorDataStatistics(Page<TutorDataStatisticsVO> page, TutorDataStatisticsSearchParamVO param) {
		tutorInfoMapper.tutorDataStatistics(page,param);
	}

}
