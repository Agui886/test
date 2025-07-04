package com.f.stock.schema;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import config.RedisDbTypeEnum;
import enums.StockMarketTypeEnum;
import enums.StockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import redis.RedisKeyPrefix;
import service.SysParamConfigService;
import utils.EastMoneyApiAnalysis;
import utils.HttpClientRequest;
import utils.RedisDao;
import utils.StringUtil;
import vo.server.StockData;

@Component
@Slf4j
public class EastMoneyStockMarketTask {

	@Resource
	private RedisDao redisDao;
	@Resource
	private SysParamConfigService sysParamConfigService;

	/**
	 * 获取A股涨幅排行数据-每隔5分钟执行一次
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void getAStockQuoteList() {
		//执行A股涨幅排行榜数据请求
		doGetStockQuoteList(StockMarketTypeEnum.Market_A.getCode(), RedisKeyPrefix.getAStockQuoteListKey());
	}
	
	/**
	 * 获取美股涨幅排行数据-每隔5分钟执行一次
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void getUsStockQuoteList() {
		//执行-美股涨幅排行数据请求
		doGetStockQuoteList(StockMarketTypeEnum.Market_Us.getCode(), RedisKeyPrefix.getUsStockQuoteListKey());
	}
	
	/**
	 * 获取港股涨幅排行数据-每隔5分钟执行一次
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void getHkStockQuoteList() {
		//执行-港股涨幅排行数据请求
		doGetStockQuoteList(StockMarketTypeEnum.Market_Hk.getCode(), RedisKeyPrefix.getHkStockQuoteListKey());

	}
	
	/**
	 * 执行-股票涨幅排行榜请求
	 * @param stockMarketTypeCode 股票市场类型
	 * @param redisKey 缓存key
	 * @throws Exception 
	 */
	private void doGetStockQuoteList(int stockMarketTypeCode,String redisKey) {
		log.info("====================从东财获取"+StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"涨幅排行数据任务开始====================");
		try {
			//1、判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(stockMarketTypeCode, RedisDbTypeEnum.STOCK_API, redisKey)) {
				log.info(StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"已收盘，暂不处理");
				return;
			}
			//2、发起请求
			String stockType = null;
			StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
			switch(stockMarketTypeEnum) {
			case Market_Hk://港股,股票类型hk
				stockType = StockTypeEnum.HK.getCode();
				break;
			case Market_Us://美股，股票类型us
				stockType = StockTypeEnum.US.getCode();
				break;
			default ://默认A股设为null
				stockType = null;
				break;
			}
			List<StockData> stockQuoteList = (List<StockData>) EastMoneyApiAnalysis.getStockList(stockType,StockData.class);
			//3、存入缓存
			if(stockQuoteList.size() > 0) {
				redisDao.setBean(redisKey, stockQuoteList);
			}
		} catch(Exception ex) {
			log.error("从东财获取"+StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"涨幅排行数据任务异常", ex);
		} finally {
			log.info("====================从东财获取"+StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"涨幅排行数据任务结束====================");
		}
		
	}
}
