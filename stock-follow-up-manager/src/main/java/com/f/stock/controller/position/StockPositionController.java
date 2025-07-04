package com.f.stock.controller.position;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.UserStockClosingPositionService;
import service.UserStockPositionService;
import vo.manager.StockClosingPositionListVO;
import vo.manager.StockPositionListSearchParamVO;
import vo.manager.StockPositionListVO;

@RestController
@RequestMapping("/position/stock")
@Api(tags = "持仓管理")
public class StockPositionController extends BaseController {
	
	@Resource
	private UserStockPositionService userStockPositionService;
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;
	
	@ApiOperation("股票持仓")
	@PostMapping("/list")
	public Response<Page<StockPositionListVO>> list(@RequestBody StockPositionListSearchParamVO param) {
		Page<StockPositionListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userStockPositionService.managerStockPositionList(page, param, false);
		return Response.successData(page);
	}
	
	@ApiOperation("股票持仓-锁仓")
	@PostMapping("/lock")
	public Response<Void> lock(@ApiParam("持仓ID") @RequestParam("id") Integer id, @ApiParam("锁仓原因") @RequestParam(value = "lockMsg", required = false) String lockMsg) {
		return userStockPositionService.lock(id, lockMsg, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票持仓-解锁")
	@PostMapping("/unlock")
	public Response<Void> unlock(@ApiParam("持仓ID") @RequestParam("id") Integer id) {
		return userStockPositionService.unlock(id, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票持仓-修改锁仓天数")
	@PostMapping("/lockInPeriodEdit")
	public Response<Void> lockInPeriodEdit(@ApiParam("持仓ID") @RequestParam("id") Integer id, @ApiParam("天数") @RequestParam("lockInPeriod") Integer lockInPeriod) {
		return userStockPositionService.lockInPeriodEdit(id, lockInPeriod, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票持仓-强制平仓")
	@PostMapping("/close")
	public Response<Void> closePosition(@ApiParam("持仓ID") @RequestParam("id") Integer id) {
		return userStockPositionService.managerClosePosition(id, getIp(), getUser().getOperator());
	}
	
	@ApiOperation("股票持仓-平仓单")
	@PostMapping("/closingList")
	public Response<Page<StockClosingPositionListVO>> closingList(@RequestBody StockPositionListSearchParamVO param) {
		Page<StockClosingPositionListVO> page = new Page<>(param.getPageNo(), param.getPageSize());
		userStockClosingPositionService.managerStockClosingList(page, param, false);
		return Response.successData(page);
	}
}
