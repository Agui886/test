package com.f.stock.controller.market;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import annotation.NotNeedAuth;
import entity.common.Response;
import entity.common.StockCompanyInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.StockCompanyInfoService;
import service.StockInfoService;
import service.SysParamConfigService;
import service.UserStockClosingPositionService;
import service.UserStockPendingService;
import service.UserStockPositionService;
import utils.EastMoneyApi;
import utils.EastMoneyApiAnalysis;
import vo.common.StockKChartVO;
import vo.common.TokenUserVO;
import vo.server.BuyStockParamVO;
import vo.server.BuyingStockPageVO;
import vo.server.SellingStockPageVO;
import vo.server.StockDetailVO;
import vo.server.UserStockPositionListVO;

@Controller
@RequestMapping("/market")
@Api(tags = "交易和市场")
public class StockController extends BaseController {

	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private StockCompanyInfoService stockCompanyInfoService;

	@Resource
	private SysParamConfigService sysParamConfigService;
	
	@Resource
	private UserStockPendingService userStockPendingService;
	
	@Resource
	private UserStockPositionService userStockPositionService;
	
	@Resource
	private UserStockClosingPositionService userStockClosingPositionService;

	@ApiOperation("股票信息")
	@PostMapping("/stockDetail")
	@NotNeedAuth
	@ResponseBody
	public Response<StockDetailVO> stockDetail(
			@ApiParam("股票代码") @RequestParam(value = "stockCode") String stockCode,
			@ApiParam("股票类型") @RequestParam(value = "stockType") String stockType) {
		TokenUserVO tu = super.getTokenUser();
		Integer userId = tu == null ? 0 : tu.getLoginId();
		return stockInfoService.getStockDetail(userId, stockCode, stockType);
	}
	
	@ApiOperation("股票公司信息")
	@PostMapping("/stockCompany")
	@NotNeedAuth
	@ResponseBody
	public Response<StockCompanyInfo> stockCompany(
			@ApiParam("股票代码") @RequestParam(value = "stockCode") String stockCode,
			@ApiParam("股票类型") @RequestParam(value = "stockType") String stockType) {
		StockCompanyInfo s = EastMoneyApi.getStockCompanyInfo(stockType, stockCode);
		return Response.successData(s);
	}
	
	@ApiOperation("股票K线图")
	@PostMapping("/stockKChart")
	@NotNeedAuth
	@ResponseBody
	public Response<StockKChartVO> stockKChart(
			@ApiParam("股票代码") @RequestParam(value = "stockCode") String stockCode,
			@ApiParam("股票类型") @RequestParam(value = "stockType") String stockType,
			@ApiParam("K线图类型：(0-分时，1-日K，2-周K，3-月K)") @RequestParam(value = "candlestickChartType") Integer candlestickChartType) {
		StockKChartVO s = EastMoneyApiAnalysis.getStockKChart(stockType, stockCode,candlestickChartType);
		return Response.successData(s);
	}
	
	@ApiOperation("股票-买入-页面数据加载")
	@PostMapping("/buyingStockPage")
	@ResponseBody
	public Response<BuyingStockPageVO> buyingStockPage(
			@ApiParam("股票代码") @RequestParam(value = "stockCode") String stockCode,
			@ApiParam("股票类型") @RequestParam(value = "stockType") String stockType) {
		TokenUserVO tu = super.getTokenUser();
		return userStockPendingService.buyingStockPage(tu.getLoginId(), stockCode, stockType);
	}
	
	@ApiOperation("股票-买入")
	@PostMapping("/buyStock")
	@ResponseBody
	public Response<Void> buyStock(@RequestBody BuyStockParamVO param) {
		TokenUserVO tu = super.getTokenUser();
		return userStockPendingService.buyStock(tu.getLoginId(), param, super.getIp());
	}
	
	@ApiOperation("股票-卖出-加载持仓列表")
	@PostMapping("/positionList")
	@ResponseBody
	public Response<Page<UserStockPositionListVO>> positionList(
			@ApiParam("股票类型") @RequestParam(value = "stockType") String stockType,
			@ApiParam("股票名称") @RequestParam(value = "stockCode") String stockCode,
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize) {
		Page<UserStockPositionListVO> page = new Page<>(pageNo, pageSize);
		userStockPositionService.stockPositionList(page, super.getTokenUser().getLoginId(), stockType, stockCode);
		return Response.successData(page);
	}
	
	@ApiOperation("股票-卖出-页面数据加载")
	@PostMapping("/sellingStockPage")
	@ResponseBody
	public Response<SellingStockPageVO> sellingStockPage(@ApiParam("持仓id") @RequestParam(value = "id") Integer id) {
		return this.userStockClosingPositionService.sellingStockPage(id, super.getTokenUser().getLoginId());
	}
	
	@ApiOperation("股票-卖出")
	@PostMapping("/sellStock")
	@ResponseBody
	public Response<Void> sellStock(@ApiParam("持仓id") @RequestParam(value = "id") Integer id, @ApiParam("卖出股数") @RequestParam(value = "shares") Integer shares) {
		return this.userStockClosingPositionService.sellStock(id, super.getTokenUser().getLoginId(), shares, super.getIp());
	}
	
	

	

	

	

}
