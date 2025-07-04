package com.f.stock.controller.agent;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.UserRebateDetailService;
import vo.manager.RebateDetailListVO;
import vo.manager.RebateDetailParamVO;
import vo.manager.RebateRecordParamVO;
import vo.manager.RebateRecordVO;

@RestController
@RequestMapping("/agent/rebate")
@Api(tags = "代理管理")
public class RebateController {
	
	@Resource
	private UserRebateDetailService userRebateDetailService;
	
	@ApiOperation("代理返佣记录")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<RebateRecordVO>> list(@RequestBody RebateRecordParamVO param) {
		Page<RebateRecordVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userRebateDetailService.managerList(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("代理返佣记录-返佣明细")
	@PostMapping("/detailList")
	@ResponseBody
	public Response<Page<RebateDetailListVO>> detailList(@RequestBody RebateDetailParamVO param) {
		Page<RebateDetailListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userRebateDetailService.managerDetailList(page, param);
		return Response.successData(page);
	}
}
