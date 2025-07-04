package com.f.stock.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import config.RedisDbTypeEnum;
import enums.StockMarketTypeEnum;
import lombok.extern.slf4j.Slf4j;
import redis.RedisKeyPrefix;
import service.SysParamConfigService;
import utils.RedisDao;
import utils.StringUtil;
import vo.server.StockData;

@Component
@Slf4j
public class EastMoneyStockHotRankTask {

	private final String url = "https://emappdata.eastmoney.com/stockrank/getAllCurrentList";
	
	private final String usOrHkUrl = "https://emappdata.eastmoney.com/stockrank/getAllCurrHkUsList";

	@Resource
	private RedisDao redisDao;
	
	@Resource
	private SysParamConfigService sysParamConfigService;

	/**
	 * A股、港股、美股的热点股票排行榜-每隔1分钟执行一次
	 */
	@Async("stockTask")
	@Scheduled(fixedDelay = 60*1000)
	public void getStockHotRank() {
		log.info("获取东方财富A股、港股和美股的热点股票排行榜任务开始");
		try {
			//执行-A股热点股票排行榜请求
			doGetStockHotRank(StockMarketTypeEnum.Market_A.getCode());
			//执行-港股热点股票排行榜请求
			doGetStockHotRank(StockMarketTypeEnum.Market_Hk.getCode());
			//执行-美股热点股票排行榜请求
			doGetStockHotRank(StockMarketTypeEnum.Market_Us.getCode());
		} catch (Exception ex) {
			log.info("获取东方财富A股、港股和美股的热点股票排行榜任务异常", ex);
		}
		log.info("获取东方财富A股、港股和美股的热点股票排行榜任务结束");
	}

	/**
	 * 执行-热点股票排行请求
	 * @param stockMarketTypeCode
	 * @throws Exception
	 */
	private void doGetStockHotRank(int stockMarketTypeCode) {
		//设置-默认请求body
		JSONObject json  = setStockHotRankRequestBody();
		//判断股票市场类型
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		String requestUrl = "";
		String stockType = "";
		String redisKey = "";
		switch (stockMarketTypeEnum) {
		case Market_Hk://港股市场
			requestUrl = usOrHkUrl;
			json.set("marketType", "000003");
			stockType = "hk";
			redisKey = RedisKeyPrefix.getHkStockHotRankKey();
			break;
		case Market_Us://美股市场
			requestUrl = usOrHkUrl;
			json.set("marketType", "000004");
			stockType = "us";
			redisKey = RedisKeyPrefix.getUsStockHotRankKey();
			break;
		default://A股市场
			requestUrl = url;
			redisKey = RedisKeyPrefix.getAStockHotRankKey();
			break;
		}
		//判断请求限制
		if (!sysParamConfigService.getMarketRequestRestrict(stockMarketTypeCode, RedisDbTypeEnum.STOCK_API, redisKey)) {
			log.info(StockMarketTypeEnum.getNameByCode(stockMarketTypeCode)+"股已收盘，暂不处理");
			return;
		}
		//发起请求获取返回结果
		String result = HttpRequest.post(requestUrl).body(json.toString()).execute().body();
		//处理返回结果
		doStockHotRankResult(result,stockMarketTypeCode,stockType,redisKey);
	}
	
	/**
	 * 设置-默认热点股票排行榜请求body
	 * @return
	 */
	private JSONObject setStockHotRankRequestBody() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Referer", "https://vipmoney.eastmoney.com/");
		headers.put("Origin", "https://vipmoney.eastmoney.com");
		headers.put("User-Agent",
				"Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36");
		JSONObject json = new JSONObject();
		json.set("appId", "appId01");
		json.set("globalId", "786e4c21-70dc-435a-93bb-38");
		json.set("marketType", "");
		json.set("pageNo", "1");
		json.set("pageSize", "100");
		return json;
	}
	
	/**
	 * 处理返回结果
	 * @param ret
	 * @param stockMarketTypeCode
	 * @param stockType
	 */
	private void doStockHotRankResult(String ret,int stockMarketTypeCode,String stockType,String redisKey) {
		if (StringUtil.isEmpty(ret)) {
			log.info("获取东方财富" + StockMarketTypeEnum.getNameByCode(stockMarketTypeCode) + "-热点排行榜数据无响应");
		} else {
			//解析返回结果
			JSONObject response = new JSONObject(ret);
			int code = response.getInt("code");
			if (code == 0) {
				JSONArray data = response.getJSONArray("data");
				List<StockData> list = new ArrayList<>();
				for (Object o : data) {
					JSONObject i = new JSONObject(o);
					StockData sd = new StockData();
					if (stockMarketTypeCode == StockMarketTypeEnum.Market_A.getCode()) {//A股-解析
						String gid = i.getStr("sc").toLowerCase();
						sd.setStockType(gid.substring(0, 2));
						sd.setStockCode(gid.substring(2));
					}else {//港股/美股-解析
						String[] codeArr = i.getStr("sc").split("\\|");
						sd.setStockType(stockType);
						sd.setStockCode(codeArr[1]);
					}
					list.add(sd);
				}
				//存缓存
				redisDao.setBean(redisKey, list);
			} else {
				log.info("获取东方财富" + StockMarketTypeEnum.getNameByCode(stockMarketTypeCode) + "-热点排行榜数据失败，返回数据：{}", ret);
			}
		}
	}

}
