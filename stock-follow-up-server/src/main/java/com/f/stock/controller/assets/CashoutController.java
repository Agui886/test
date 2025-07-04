package com.f.stock.controller.assets;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.CashoutRecord;
import entity.UserBankInfo;
import entity.common.Response;
import enums.AmtDeTypeEnum;
import enums.CurrencyEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.CashoutRecordService;
import service.IpAddressService;
import service.UserBankInfoService;
import service.UserInfoService;
import vo.common.TokenUserVO;
import vo.server.CashinAndOutRecordParamVO;
import vo.server.CashoutRecordVO;

@Controller
@RequestMapping("/assets/cashout")
@Api(tags = "资产")
public class CashoutController extends BaseController {
	
	@Resource 
	private CashoutRecordService cashoutRecordService;
	
	@Resource
	private UserBankInfoService userBankInfoService;
	
	@Resource
	private UserInfoService userInfoService;
	
	@Resource
	private IpAddressService ipAddressService;
	
	@ApiOperation("出金-银行卡信息")
	@PostMapping("/bank")
	@ResponseBody
	public Response<UserBankInfo> bank() {
		TokenUserVO tu = super.getTokenUser();
		UserBankInfo ubi = userBankInfoService.lambdaQuery().eq(UserBankInfo::getUserId, tu.getLoginId()).one();
		return Response.successData(ubi);
	}
	
	@ApiOperation("出金-添加、修改银行卡")
	@PostMapping("/bank/add")
	@ResponseBody
	public Response<Void> addBank(@RequestBody UserBankInfo ubi) {
		TokenUserVO tu = super.getTokenUser();
		return userBankInfoService.add(tu.getLoginId(), ubi, getIp(), tu.getOperator());
	}
	
	@ApiOperation("出金-提交")
	@PostMapping("/submit")
	@ResponseBody
	public Response<Void> submit(
			@RequestParam(value = "amount") BigDecimal amount, 
			@RequestParam(value = "fundPwd") String fundPwd) {
		return cashoutRecordService.submit(super.getTokenUser().getLoginId(), amount, fundPwd, super.getIp());
	}
	
	@ApiOperation("出金-记录")
	@PostMapping("/record")
	@ResponseBody
	public Response<Page<CashoutRecordVO>> record(@RequestBody CashinAndOutRecordParamVO param) {
		Page<CashoutRecordVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		TokenUserVO tu = super.getTokenUser();
		param.setUserId(tu.getLoginId());
		cashoutRecordService.record(page, param);
		return Response.successData(page);
	}
	
	@ApiOperation("出金-取消")
	@PostMapping("/cancel")
	@ResponseBody
	public Response<Void> cannel(@ApiParam("提现订单id") @RequestParam(value = "id") Integer id) {
		TokenUserVO tu = super.getTokenUser();
		CashoutRecord cashinRecord = cashoutRecordService.getById(id);
		if(cashinRecord == null) {
			return Response.fail("充值记录不存在");
		}
		if (cashinRecord.getOrderStatus() != 0) {
			return Response.fail("订单状态必须是待审核！");
		}
		cashoutRecordService.lambdaUpdate()
		.eq(CashoutRecord::getId, id)
		.set(CashoutRecord::getOrderStatus, 2).update();
		String ip = super.getIp();
		userInfoService.updateUserAvailableAmt(cashinRecord.getUserId(), AmtDeTypeEnum.BankCardWithdrawalDeclined, cashinRecord.getOrderAmount().add(cashinRecord.getFee()), "", CurrencyEnum.CNY, BigDecimal.ONE, ip, ipAddressService.getIpAddress(ip).getAddress2(), tu.getOperator(),cashinRecord.getOrderSn());
		return Response.success();
	}
}
