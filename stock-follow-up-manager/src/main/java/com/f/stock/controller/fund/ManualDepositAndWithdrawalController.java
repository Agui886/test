package com.f.stock.controller.fund;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.f.stock.controller.BaseController;

import entity.CashinRecord;
import entity.CashoutRecord;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import enums.CashinTypeEnum;
import enums.CashoutTypeEnum;
import enums.CurrencyEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.IpAddressService;
import service.UserInfoService;
import utils.OrderNumberGenerator;
import vo.manager.UserAmtDetailVO;

@RestController
@RequestMapping("/fund/manual")
@Api(tags = "资金管理")
public class ManualDepositAndWithdrawalController extends BaseController {
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@ApiOperation("人工充值-用户查询")
    @PostMapping("/userSearch")
    @ResponseBody
	public Response<UserAmtDetailVO> userSearch(@ApiParam("用户id") @RequestParam("user")Integer userId) {
		return Response.successData(userInfoService.getUserAmtDetail(userId));
	} 
	@ApiOperation("人工充值-充值")
    @PostMapping("/deposit")
    @ResponseBody
	public Response<Void> deposit(
			@ApiParam("用户id") @RequestParam("user") Integer userId,
			@ApiParam("金额") @RequestParam("amt") BigDecimal amt,
			@ApiParam("备注") @RequestParam(value = "remark", required = false) String remark
			) {
		if(amt.compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入正确的金额");
		}
		String ip = getIp();
		String addr = ipAddressService.getIpAddress(ip).getAddress2();
		String operator = getUser().getOperator();
		Date now = new Date();
		String orderSn = OrderNumberGenerator.create(2);
		userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.ManualRecharge, amt, remark, CurrencyEnum.CNY, BigDecimal.ONE, ip, addr, operator,orderSn);
		CashinRecord cr = new CashinRecord();
		cr.setUserId(userId);
		cr.setOrderSn(orderSn);
		cr.setOrderAmount(amt);
		cr.setFinalAmount(amt);
		cr.setRequestTime(now);
		cr.setOrderStatus(1);
		cr.setOperateTime(now);
		cr.setOperator(operator);
		cr.setRemark(remark);
		cr.setCashinTypeCode(CashinTypeEnum.ManualRecharge.getCode());
		cr.setCashinTypeName(CashinTypeEnum.ManualRecharge.getName());
		cr.insert();
		return Response.success();
	} 
	@ApiOperation("人工充值-提现")
    @PostMapping("/withdrawal")
    @ResponseBody
	public Response<Void> withdrawal(
			@ApiParam("用户id") @RequestParam("user") Integer userId,
			@ApiParam("金额") @RequestParam("amt") BigDecimal amt,
			@ApiParam("备注") @RequestParam(value = "remark", required = false) String remark) {
		if(amt.compareTo(BigDecimal.ZERO) < 1) {
			return Response.fail("请输入正确的金额");
		}
		String ip = getIp();
		String addr = ipAddressService.getIpAddress(ip).getAddress2();
		String operator = getUser().getOperator();
		Date now = new Date();
		String orderSn = OrderNumberGenerator.create(3);
		userInfoService.updateUserAvailableAmt(userId, AmtDeTypeEnum.ManualDeduction, amt, remark, CurrencyEnum.CNY, BigDecimal.ONE, ip, addr, operator,orderSn);
		CashoutRecord cr = new CashoutRecord();
		cr.setUserId(userId);
		cr.setOrderSn(OrderNumberGenerator.create(3));
		cr.setOrderAmount(amt);
		cr.setFinalAmount(amt);
		cr.setRequestTime(now);
		cr.setOrderStatus(1);
		cr.setOperateTime(now);
		cr.setOperator(operator);
		cr.setRemark(remark);
		cr.setCashoutTypeCode(CashoutTypeEnum.ManualRecharge.getCode());
		cr.setCashoutTypeName(CashoutTypeEnum.ManualRecharge.getName());
		cr.insert();
		return Response.success();
	} 
}
