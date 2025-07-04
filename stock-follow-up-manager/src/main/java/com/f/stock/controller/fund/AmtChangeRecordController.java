package com.f.stock.controller.fund;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserAmtChangeRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserAmtChangeRecordService;
import vo.manager.UserAmtChangeRecordSearchParamVO;

@RestController
@RequestMapping("/fund/amtChangeRecord")
@Api(tags = "资金管理")
public class AmtChangeRecordController {
	
	@Resource
	private UserAmtChangeRecordService userAmtChangeRecordService;
	
	@ApiOperation("资金记录")
    @PostMapping("/list")
    @ResponseBody
	public Response<Page<UserAmtChangeRecord>> list(@RequestBody UserAmtChangeRecordSearchParamVO param) {
		Page<UserAmtChangeRecord> page = new Page<>(param.getPageNo(), param.getPageSize());
		userAmtChangeRecordService.managerList(page, param);
		return Response.successData(page);
	}
}
