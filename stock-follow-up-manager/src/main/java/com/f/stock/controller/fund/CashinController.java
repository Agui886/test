package com.f.stock.controller.fund;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.f.stock.controller.BaseController;

import entity.CashinRecord;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.CashinRecordService;
import vo.manager.CashinRecordListParamVO;
import vo.manager.CashinRecordListVO;

@RestController
@RequestMapping("/fund/cashin")
@Api(tags = "资金管理")
public class CashinController extends BaseController {
	
	@Resource
	private CashinRecordService cashinRecordService;
	
	@ApiOperation("充值列表")
    @PostMapping("/list")
    @ResponseBody
	public Response<CashinRecordListVO> list(@RequestBody CashinRecordListParamVO param) {
		return cashinRecordService.managerList(param);
	}
	
	@ApiOperation("充值列表-通过")
    @PostMapping("/pass")
    @ResponseBody
	public Response<Void> pass(@ApiParam("充值订单id") @RequestParam("id")Integer id) {
		CashinRecord cashinRecord = cashinRecordService.getById(id);
		if(cashinRecord == null) {
			return Response.fail("充值记录不存在");
		}
		cashinRecord.setFinalAmount(cashinRecord.getOrderAmount());
		cashinRecord.setOrderStatus(1);
		cashinRecord.updateById();
		return Response.success();
	}
	@ApiOperation("充值列表-拒绝")
    @PostMapping("/reject")
    @ResponseBody
	public Response<Void> reject(@ApiParam("充值订单id") @RequestParam("id")Integer id) {
		CashinRecord cashinRecord = cashinRecordService.getById(id);
		if(cashinRecord == null) {
			return Response.fail("充值记录不存在");
		}
		cashinRecord.setOrderStatus(2);
		cashinRecord.updateById();
		return Response.success();
	}
}
