package com.f.stock.controller.market;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import annotation.NotNeedAuth;
import entity.common.Response;
import enums.StockMarketTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import redis.RedisKeyPrefix;
import service.StockInfoService;
import utils.RedisDao;
import vo.server.StockData;
import vo.server.StockIndexQuotesVO;
import vo.server.StockPlateQuotesVO;

@Controller
@RequestMapping("/market")
@Api(tags = "交易和市场")
public class MarketController {
	
	@Resource
	private StockInfoService stockInfoService;
	
	@Resource
	private RedisDao redisDao;
	
	@ApiOperation("股票-列表")
	@PostMapping("/stockList")
	@NotNeedAuth
	@ResponseBody
	public Response<Page<StockData>> stockList(
			@ApiParam("股票市场类型代码(0:A股，1：港股，2：美股)") @RequestParam(value = "stockMarketTypeCode", defaultValue = "0", required = false) Integer stockMarketTypeCode,
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize,
			@ApiParam("排序条件(最新价：nowPrice,涨幅比例：percentageIncrease),成交量：turnover") @RequestParam(value = "sortTerm", defaultValue = "percentageIncrease", required = false) String sortTerm,
			@ApiParam("排序类型(升序：asc，降序：desc)") @RequestParam(value = "sortType", defaultValue = "desc", required = false) String sortType
			) {
		Page<StockData> page = stockInfoService.getStockQuoteListPage(pageNo, pageSize, sortTerm, sortType,stockMarketTypeCode);
		return Response.successData(page);
	}
	
	@ApiOperation("股票-指数行情")
	@PostMapping("/stockIndexQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockIndexQuotesVO>> stockIndexQuotes(
			@ApiParam("股票市场类型代码(0:A股，1：港股，2：美股)") @RequestParam(value = "stockMarketTypeCode", defaultValue = "0", required = false) Integer stockMarketTypeCode
			) {
		String redisKeyString = RedisKeyPrefix.getAStockIndexQuotesKey();//默认A股
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		switch(stockMarketTypeEnum) {
		case Market_Hk://港股
			redisKeyString = RedisKeyPrefix.getHkStockIndexQuotesKey();
			break;
		case Market_Us://美股
			redisKeyString = RedisKeyPrefix.getUsStockIndexQuotesKey();
			break;	
		}
		List<StockIndexQuotesVO> list = redisDao.getBeanList(redisKeyString, StockIndexQuotesVO.class);
		return Response.successData(list);
	}
	
	@ApiOperation("股票-行业板块")
	@PostMapping("/stockIndustryPlateQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockPlateQuotesVO>> stockIndustryPlateQuotes(
			@ApiParam("股票市场类型代码(0:A股，1：港股，2：美股)") @RequestParam(value = "stockMarketTypeCode", defaultValue = "0", required = false) Integer stockMarketTypeCode
			) {
		String redisKeyString = RedisKeyPrefix.getAStockIndustryPlateQuotesKey();//默认A股
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		switch(stockMarketTypeEnum) {
		case Market_Hk://港股
			redisKeyString = RedisKeyPrefix.getHkStockIndustryPlateQuotesKey();
			break;
		case Market_Us://美股
			redisKeyString = RedisKeyPrefix.getUsStockPlateQuotesKey();
			break;	
		}
		List<StockPlateQuotesVO> list = redisDao.getBeanList(redisKeyString, StockPlateQuotesVO.class);
 		return Response.successData(list);
	}
	
	@ApiOperation("A股-概念板块")
	@PostMapping("/stockConceptPlateQuotes")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockPlateQuotesVO>> stockConceptPlateQuotes() {
		List<StockPlateQuotesVO> list = redisDao.getBeanList(RedisKeyPrefix.getAStockConceptPlateQuotesKey(), StockPlateQuotesVO.class);
 		return Response.successData(list);
	}
	
	@ApiOperation("股票-近一个月涨幅榜")
	@PostMapping("/increaseRateRank")
	@NotNeedAuth
	@ResponseBody
	public Response<List<StockData>> increaseRateRank(@ApiParam("股票市场类型代码") @RequestParam(value = "stockMarketTypeCode", defaultValue = "0", required = false) Integer stockMarketTypeCode
			) {
		return Response.successData(stockInfoService.getIncreaseRateRank(1,20,stockMarketTypeCode));
	}
}
