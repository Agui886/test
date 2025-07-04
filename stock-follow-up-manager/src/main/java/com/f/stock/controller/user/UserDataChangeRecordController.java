package com.f.stock.controller.user;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserDataChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserDataChangeRecordService;
import utils.StringUtil;
import vo.manager.UserDataChangeRecordSearchParamVO;

@RestController
@RequestMapping("/userData")
@Api(tags = "用户管理")
public class UserDataChangeRecordController {
	
	@Resource
	private UserDataChangeRecordService userDataChangeRecordService;
	
	@ApiOperation("用户资料变更记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<UserDataChangeRecord>> list(@RequestBody UserDataChangeRecordSearchParamVO param) {
		Page<UserDataChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<UserDataChangeRecord> lqw = new LambdaQueryWrapper<>();
		if(param.getUserId() != null && param.getUserId() > 0) {
			lqw.eq(UserDataChangeRecord::getUserId, param.getUserId());
		}
		if(!StringUtil.isEmpty(param.getUserDataChangeTypeCode())) {
			lqw.eq(UserDataChangeRecord::getDataChangeTypeCode, param.getUserDataChangeTypeCode());
		}
		if(param.getCreateTimeStart() != null) {
			lqw.ge(UserDataChangeRecord::getCreateTime, param.getCreateTimeStart());
		}
		if(param.getCreateTimeEnd() != null) {
			lqw.le(UserDataChangeRecord::getCreateTime, param.getCreateTimeEnd());
		}
		lqw.orderByDesc(UserDataChangeRecord::getCreateTime);
		userDataChangeRecordService.page(page, lqw);
		return Response.successData(page);
	}
}
