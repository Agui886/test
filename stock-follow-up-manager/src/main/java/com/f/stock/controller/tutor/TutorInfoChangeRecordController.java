package com.f.stock.controller.tutor;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.TutorInfoChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.TutorInfoChangeRecordService;
import utils.StringUtil;
import vo.manager.TutorChangeRecordListSearchParamVO;

@RestController
@RequestMapping("/tutor/changeRecord")
@Api(tags = "导师管理")
public class TutorInfoChangeRecordController extends BaseController{
	
	@Resource
	private TutorInfoChangeRecordService tutorInfoServiceRecordService;
	
	@ApiOperation("导师变更记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<TutorInfoChangeRecord>> list(@RequestBody TutorChangeRecordListSearchParamVO param) {
		Page<TutorInfoChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<TutorInfoChangeRecord> lqw = new LambdaQueryWrapper<>();
		if (param.getTutorId() != null && param.getTutorId() > 0) {
			lqw.eq(TutorInfoChangeRecord::getTutorId, param.getTutorId());
		}
		if(param.getDataChangeTypeCode() != null) {
			lqw.eq(TutorInfoChangeRecord::getDataChangeTypeCode, param.getDataChangeTypeCode());
		}
		if (!StringUtil.isEmpty(param.getOperator())) {
			lqw.like(TutorInfoChangeRecord::getOperator, param.getOperator());
		}
		if(param.getTimeStart() != null) {
			lqw.ge(TutorInfoChangeRecord::getCreateTime, param.getTimeStart());
		}
		if(param.getTimeEnd() != null) {
			lqw.le(TutorInfoChangeRecord::getCreateTime, param.getTimeEnd());
		}
		lqw.orderByDesc(TutorInfoChangeRecord::getId);
		tutorInfoServiceRecordService.page(page, lqw);
		return Response.successData(page);
	}
}
