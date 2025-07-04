package com.f.stock.controller.tutor;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.TutorInfo;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.TutorInfoService;
import utils.StringUtil;
import vo.manager.TutorListSearchParamVO;

@RestController
@RequestMapping("/tutor")
@Api(tags = "导师管理")
public class TutorInfoController extends BaseController{
	
	@Resource
	private TutorInfoService tutorInfoService;
	
	@ApiOperation("导师列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<TutorInfo>> list(@RequestBody TutorListSearchParamVO param) {
		Page<TutorInfo> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<TutorInfo> lqw = new LambdaQueryWrapper<>();
		lqw.select(TutorInfo::getId, TutorInfo::getTutorName, TutorInfo::getDomainType, TutorInfo::getTutorProfile,
				TutorInfo::getSort, TutorInfo::getConfigurationJson, TutorInfo::getIsEnabled, TutorInfo::getCreateTime,
				TutorInfo::getCreator, TutorInfo::getExperience);
		if (!StringUtil.isEmpty(param.getTutor())) {
			lqw.or(l -> l.eq(TutorInfo::getId, param.getTutor()).or().like(TutorInfo::getTutorName, param.getTutor()));
		}
		if(param.getDomainType() != null) {
			lqw.eq(TutorInfo::getDomainType, param.getDomainType());
		}
		if(param.getIsEnabled() != null) {
			lqw.eq(TutorInfo::getIsEnabled, param.getIsEnabled());
		}
		if (!StringUtil.isEmpty(param.getCreator())) {
			lqw.like(TutorInfo::getCreator, param.getCreator());
		}
		if(param.getCreateTimeStart() != null) {
			lqw.ge(TutorInfo::getCreateTime, param.getCreateTimeStart());
		}
		if(param.getCreateTimeEnd() != null) {
			lqw.le(TutorInfo::getCreateTime, param.getCreateTimeEnd());
		}
		if(param.getIsEnabled() != null) {
			lqw.eq(TutorInfo::getIsEnabled, param.getIsEnabled());
		}
		lqw.orderByAsc(TutorInfo::getSort);
		tutorInfoService.page(page, lqw);
		return Response.successData(page);
	}
	
	@ApiOperation("导师列表-添加导师")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody TutorInfo tutorInfo) {
		return this.tutorInfoService.add(tutorInfo, super.getIp(), super.getUser().getOperator());
	}
	
	@ApiOperation("导师列表-导师详情")
	@PostMapping("/detail")
	@ResponseBody
	public Response<TutorInfo> detail(@ApiParam("导师id")@RequestParam("id") Integer id) {
		TutorInfo ret = this.tutorInfoService.getById(id);
		return Response.successData(ret);
	}
	
	@ApiOperation("导师列表-修改导师")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody TutorInfo tutorInfo) {
		return this.tutorInfoService.edit(tutorInfo, super.getIp(), super.getUser().getOperator());
	}
	
	@ApiOperation("导师列表-删除导师")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("导师id") @RequestParam("id") Integer id) {
		return this.tutorInfoService.delete(id, super.getIp(), super.getUser().getOperator());
	}
	
	@ApiOperation("导师列表-修改跟投状态")
	@PostMapping("/updateEnableStatus")
	@ResponseBody
	public Response<Void> updateEnableStatus(@ApiParam("导师id") @RequestParam("id") Integer id, @ApiParam("状态值，1-开启，0-禁止") @RequestParam("status") Boolean status) {
		return this.tutorInfoService.updateEnableStatus(id, status, super.getIp(), super.getUser().getOperator());
	}
}
