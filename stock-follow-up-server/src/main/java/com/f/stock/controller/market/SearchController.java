package com.f.stock.controller.market;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import annotation.NotNeedAuth;
import entity.SiteHotStock;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SiteHotStockService;
import service.StockInfoService;
import vo.common.TokenUserVO;
import vo.server.SearchStockResultVO;
import vo.server.StockData;

@Controller
@RequestMapping("/market/search")
@Api(tags = "交易和市场")
public class SearchController extends BaseController {
	
	@Resource
	private SiteHotStockService siteHotStockService;
	
	@Resource
	private StockInfoService stockInfoService;
	
	@ApiOperation("热门搜索-热门股票列表")
	@PostMapping("/hotSearchStockList")
	@ResponseBody
	public Response<List<SiteHotStock>> hotSearchStockList() {
		return siteHotStockService.hotSearchStockList();
	}
	
	@ApiOperation("热门搜索-股票查询结果列表")
	@PostMapping("/stockResultByKeywords")
	@ResponseBody
	public Response<Page<SearchStockResultVO>> stockResultByKeywords (
				@ApiParam("搜索词") @RequestParam(value = "keywords", required = false) String keywords,
				@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
				@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize
			) {
		Integer userId = 0;
		TokenUserVO tu = super.getTokenUser();
		if(tu != null) {
			userId = tu.getLoginId();
		}
		return siteHotStockService.stockResultByKeywords(userId, keywords, pageNo, pageSize);
	}
	
	@ApiOperation("热门搜索-近一个月涨幅榜")
	@PostMapping("/increaseRateRank")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockData>> increaseRateRank() {
		return Response.successData(stockInfoService.getIncreaseRateRank(1,10,null));
	}
}
