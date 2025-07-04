package com.f.stock.controller.fund;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.f.stock.controller.BaseController;

import entity.CashoutRecord;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.CashoutRecordService;
import service.IpAddressService;
import service.UserInfoService;
import vo.manager.CashoutRecordListParamVO;
import vo.manager.CashoutRecordListVO;

@RestController
@RequestMapping("/fund/cashout")
@Api(tags = "资金管理")
public class CashoutController extends BaseController {
	
	@Resource
	private CashoutRecordService cashoutRecordService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@ApiOperation("提现列表")
    @PostMapping("/list")
    @ResponseBody
	public Response<CashoutRecordListVO> list(@RequestBody CashoutRecordListParamVO param) {
		return cashoutRecordService.managerList(param);
	}
	
	@ApiOperation("提现列表-通过")
    @PostMapping("/pass")
    @ResponseBody
	public Response<Void> pass(@ApiParam("提现订单id") @RequestParam("id")Integer id) {
		CashoutRecord cashinRecord = cashoutRecordService.getById(id);
		if(cashinRecord == null) {
			return Response.fail("充值记录不存在");
		}
		if (cashinRecord.getOrderStatus() != 0) {
			return Response.fail("订单状态必须是待审核！");
		}
		cashoutRecordService.lambdaUpdate()
			.eq(CashoutRecord::getId, id)
			.set(CashoutRecord::getFinalAmount, cashinRecord.getOrderAmount().subtract(cashinRecord.getFee()))
			.set(CashoutRecord::getOrderStatus, 1).update();
		return Response.success();
	}
	@ApiOperation("提现列表-拒绝")
    @PostMapping("/reject")
    @ResponseBody
    @Transactional
	public Response<Void> reject(@ApiParam("提现订单id") @RequestParam("id")Integer id, @ApiParam("备注") @RequestParam("remark")String remark) {
		CashoutRecord cashinRecord = cashoutRecordService.getById(id);
		if(cashinRecord == null) {
			return Response.fail("充值记录不存在");
		}
		if (cashinRecord.getOrderStatus() != 0) {
			return Response.fail("订单状态必须是待审核！");
		}
		cashoutRecordService.lambdaUpdate()
		.eq(CashoutRecord::getId, id)
		.set(CashoutRecord::getOrderStatus, 3)
		.set(CashoutRecord::getRemark, remark)
		.update();
		String ip = super.getIp();
		userInfoService.updateUserAvailableAmt(cashinRecord.getUserId(), AmtDeTypeEnum.BankCardWithdrawalDeclined, cashinRecord.getOrderAmount().add(cashinRecord.getFee()), "", CurrencyEnum.CNY, BigDecimal.ONE, ip, ipAddressService.getIpAddress(ip).getAddress2(), super.getUser().getOperator(),cashinRecord.getOrderSn());
		return Response.success();
	}
}
