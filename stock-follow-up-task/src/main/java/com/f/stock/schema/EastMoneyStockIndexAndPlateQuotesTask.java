package com.f.stock.schema;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
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
import service.StockInfoService;
import service.SysParamConfigService;
import utils.HttpClientRequest;
import utils.RedisDao;
import utils.StringUtil;
import vo.server.StockIndexQuotesVO;
import vo.server.StockPlateQuotesVO;

@Component
@Slf4j
public class EastMoneyStockIndexAndPlateQuotesTask {
	
	@Resource
	private RedisDao redisDao;
	@Resource
	private SysParamConfigService sysParamConfigService;
	
	private final String aStockIndexQuotesUrl = "http://push2delay.eastmoney.com/api/qt/ulist.np/get?fltt=2&invt=2&fields=f1,f2,f3,f4,f12,f13,f14&secids=1.000001,0.399001,0.399006,1.000300,0.399005&ut=f057cbcbce2a86e2866ab8877db1d059&forcect=1&wbp2u=|0|0|0|wap";
	private final String hkStockIndexQuotesUrl = "http://push2delay.eastmoney.com/api/qt/ulist.np/get?fltt=2&invt=2&fields=f1,f2,f3,f4,f12,f13,f14&secids=100.HSI,100.HSCEI,100.HSAHP&ut=f057cbcbce2a86e2866ab8877db1d059&forcect=1";
	private final String usStockIndexQuotesUrl = "http://push2delay.eastmoney.com/api/qt/ulist.np/get?fltt=2&invt=2&fields=f1,f2,f3,f4,f12,f13,f14&secids=100.DJIA,100.NDX,100.SPX&ut=f057cbcbce2a86e2866ab8877db1d059&forcect=1";
	
	private final String aStockIndustryPlateQuotesUrl = "https://push2delay.eastmoney.com/api/qt/clist/get?cb=jQuery34105986419528790634_1735211047835&ut=bd1d9ddb04089700cf9c27f6f7426281&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f19,f20,f21,f23,f24,f25,f22,f11,f62,f101,f104,f105,f106,f127,f128,f136,f115,f148,f152&np=1&fltt=2&invt=2&pn=1&fs=m:90+t:2&fid=f3&po=1&pz=20&_=";
	private final String aStockConceptPlateQuotesUrl = "https://push2delay.eastmoney.com/api/qt/clist/get?cb=jQuery3410009563501205726466_1735241020878&pn=1&pz=3&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50&fields=f3,f4,f12,f13,f14,f128,f136&_=";
	private final String hkStockIndustryPlateQuotesUrl = "https://push2delay.eastmoney.com/api/qt/clist/get?cb=jQuery34109551117005930863_1735241819117&fltt=2&invt=2&fs=m:201+t:2&fields=f3,f4,f12,f13,f14,f128,f136&ut=bd1d9ddb04089700cf9c27f6f7426281&np=1&pn=1&pz=3&po=1&fid=f3&_=";
	private final String usStockPlateQuotesUrl = "https://push2delay.eastmoney.com/api/qt/clist/get?cb=jQuery34108377920926819686_1735242247262&ut=bd1d9ddb04089700cf9c27f6f7426281&invt=2&fltt=2&fs=m:202&np=1&pn=1&pz=3&fid=f3&po=1&fields=f4,f12,f13,f14,f3,f128,f136&dect=1&_=";
	
	private final String stockIndexTrendSUrl = "http://push2delay.eastmoney.com/api/qt/stock/trends2/get?secid=%s&fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14,f17&fields2=f51,f53,f54,f55,f56,f57,f58&iscr=0&iscca=0&ut=f057cbcbce2a86e2866ab8877db1d059&ndays=1&cb=quotepushdata0";
	
	/**
	 * A股指数行情-每隔15s执行一次
	 */
//	@PostConstruct
	@Scheduled(fixedDelay = 15000)
	@Async("stockTask")
	public void getAStockIndexQuotes() {
		log.info("====================从东财获取A股指数行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getAStockIndexQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_A.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_A.getCode())+"股已收盘，暂不处理");
				return;
			}
			//发起请求
			String ret = HttpRequest.get(aStockIndexQuotesUrl).execute().body();
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取A股指数行情无响应");
			} else {
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockIndexQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockIndexQuotesVO siq = new StockIndexQuotesVO();
						siq.setIndexCode(i.getStr("f12"));
						siq.setIndexName(i.getStr("f14"));
						siq.setIncrease(i.getBigDecimal("f4"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setNowPrice(i.getBigDecimal("f2"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取A股指数行情失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取A股指数行情任务异常", ex);
		}
		log.info("====================从东财获取A股指数行情任务结束====================");
	}
	
	/**
	 * 港股指数行情-每隔15s执行一次
	 */
	@Scheduled(fixedDelay = 15000)
	@Async("stockTask")
	public void getHkStockIndexQuotes() {
		log.info("====================从东财获取港股指数行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getHkStockIndexQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_Hk.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_Hk.getCode())+"股已收盘，暂不处理");
				return;
			}
			//发起请求
			String ret = HttpRequest.get(hkStockIndexQuotesUrl).execute().body();
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取港股指数行情无响应");
			} else {
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockIndexQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockIndexQuotesVO siq = new StockIndexQuotesVO();
						siq.setIndexCode(i.getStr("f12"));
						siq.setIndexName(i.getStr("f14"));
						siq.setIncrease(i.getBigDecimal("f4"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setNowPrice(i.getBigDecimal("f2"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取港股指数行情失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取港股指数行情任务异常", ex);
		}
		log.info("====================从东财获取港股指数行情任务结束====================");
	}
	
	/**
	 * 美股指数行情-每隔15s执行一次
	 */
	@Async("stockTask")
	@Scheduled(fixedDelay = 15000)
	public void getUsStockIndexQuotes() {
		log.info("====================从东财获取美股指数行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getUsStockIndexQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_Us.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_Us.getCode())+"股已收盘，暂不处理");
				return;
			}
			//发起请求
			String ret = HttpRequest.get(usStockIndexQuotesUrl).execute().body();
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取美股指数行情无响应");
			} else {
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockIndexQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockIndexQuotesVO siq = new StockIndexQuotesVO();
						siq.setIndexCode(i.getStr("f12"));
						siq.setIndexName(i.getStr("f14"));
						siq.setIncrease(i.getBigDecimal("f4"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setNowPrice(i.getBigDecimal("f2"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取美股指数行情失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取美股指数行情任务异常", ex);
		} finally {
			log.info("====================从东财获取美股指数行情任务结束====================");
		}
	}
	
	/**
	 * A股行业板块行情-每隔15s执行一次
	 */
	@Scheduled(fixedDelay = 15000)
	@PostConstruct
	@Async("stockTask")
	public void getAStockIndustryPlateQuotes() {
		log.info("====================从东财获取A股行业板块行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getAStockIndustryPlateQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_A.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_A.getCode())+"股已收盘，暂不处理");
				return;
			}
			//发起请求
			String ret = HttpClientRequest.doGet(aStockIndustryPlateQuotesUrl + System.currentTimeMillis());
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取A股行业板块行情无响应");
			} else {
				ret = ret.substring(ret.indexOf("{"), ret.length() - 2);
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockPlateQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockPlateQuotesVO siq = new StockPlateQuotesVO();
						siq.setPlateName(i.getStr("f14"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setFirstStockName(i.getStr("f128"));
						siq.setFirstStockPercentageIncrease(i.getBigDecimal("f136"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取A股行业板块失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取A股行业板块任务异常", ex);
		}
		log.info("====================从东财获取A股行业板块任务结束====================");
	}
	
	/**
	 * A股概念板块行情-每隔15s执行一次
	 */
	@Scheduled(fixedDelay = 15000)
	@PostConstruct
	@Async("stockTask")
	public void getAStockConceptPlateQuotes() {
		log.info("====================从东财获取A股概念板块行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getAStockConceptPlateQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_A.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_A.getCode())+"股已收盘，暂不处理");
				return;
			}
			//发起请求
			String ret = HttpClientRequest.doGet(aStockConceptPlateQuotesUrl + System.currentTimeMillis());
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取A股概念板块行情无响应");
			} else {
				ret = ret.substring(ret.indexOf("{"), ret.length() - 2);
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockPlateQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockPlateQuotesVO siq = new StockPlateQuotesVO();
						siq.setPlateName(i.getStr("f14"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setFirstStockName(i.getStr("f128"));
						siq.setFirstStockPercentageIncrease(i.getBigDecimal("f136"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取A股概念板块失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取A股概念板块任务异常", ex);
		}
		log.info("====================从东财获取A股概念板块任务结束====================");
	}
	
	/**
	 * 港股行业板块行情-每隔15s执行一次
	 */
	@Scheduled(fixedDelay = 15000)
	@PostConstruct
	@Async("stockTask")
	public void getHkStockIndustryPlateQuotes() {
		log.info("====================从东财获取港股行业板块行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getHkStockIndustryPlateQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_Hk.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_Hk.getCode())+"股已收盘，暂不处理");
				return;
			}
			//发起请求
			String ret = HttpClientRequest.doGet(hkStockIndustryPlateQuotesUrl + System.currentTimeMillis());
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取港股行业板块行情无响应");
			} else {
				ret = ret.substring(ret.indexOf("{"), ret.length() - 2);
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockPlateQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockPlateQuotesVO siq = new StockPlateQuotesVO();
						siq.setPlateName(i.getStr("f14"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setFirstStockName(i.getStr("f128"));
						siq.setFirstStockPercentageIncrease(i.getBigDecimal("f136"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取港股行业板块失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取港股行业板块任务异常", ex);
		}
		log.info("====================从东财获取港股行业板块任务结束====================");
	}
	
	/**
	 * 美股板块行情-每隔15s执行一次
	 */
	@Async("stockTask")
	@Scheduled(fixedDelay = 15000)
	public void getUsStockPlateQuotes() {
		log.info("====================从东财获取美股板块行情任务开始====================");
		try {
			//获取缓存key
			String key = RedisKeyPrefix.getUsStockPlateQuotesKey();
			//判断请求限制
			if (!sysParamConfigService.getMarketRequestRestrict(StockMarketTypeEnum.Market_Us.getCode(), RedisDbTypeEnum.STOCK_API, key)) {
				log.info(StockMarketTypeEnum.getNameByCode(StockMarketTypeEnum.Market_Us.getCode())+"股已收盘，暂不处理");
			}
			//发起请求
			String ret = HttpClientRequest.doGet(usStockPlateQuotesUrl + System.currentTimeMillis());
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取美股板块行情无响应");
			} else {
				ret = ret.substring(ret.indexOf("{"), ret.length() - 2);
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					ArrayList<StockPlateQuotesVO> list = new ArrayList<>();
					JSONArray ja = response.getJSONObject("data").getJSONArray("diff");
					for (Object o : ja) {
						JSONObject i = new JSONObject(o);
						StockPlateQuotesVO siq = new StockPlateQuotesVO();
						siq.setPlateName(i.getStr("f14"));
						siq.setPercentageIncrease(i.getBigDecimal("f3"));
						siq.setFirstStockName(i.getStr("f128"));
						siq.setFirstStockPercentageIncrease(i.getBigDecimal("f136"));
						//增加趋势数据赋值
						siq.setTrends(getStockIndexTrend(i));
						list.add(siq);
					}
					if(list.size() > 0) {
						redisDao.setBean(key, list);
					}
				} else {
					log.info("从东财获取美股板块行情失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取美股板块行情任务异常", ex);
		} finally {
			log.info("====================从东财获取美股板块行情任务结束====================");
		}
	}
	
	/**
	 * 股票行情-某股票趋势数据
	 */
	public double[] getStockIndexTrend(JSONObject data) {
		String indexName = data.getStr("f14");
		log.info("====================从东财获取股票行情-"+indexName+"股票趋势数据====================");
		double[] trends = null;
		try {
			log.info("====================从东财获取股票行情-"+indexName+"股票趋势数据-data："+data+"====================");
			String secid = data.getStr("f13")+"."+data.getStr("f12");
			String url = String.format(stockIndexTrendSUrl, secid);
			log.info("====================从东财获取股票行情-"+indexName+"股票趋势数据-url："+url+"====================");
			String ret = HttpRequest.get(url).execute().body();
			ret = ret.substring(ret.indexOf("(")+1,ret.indexOf(")"));
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取股票行情-"+indexName+"股票趋势数据无响应");
			} else {
				JSONObject response = new JSONObject(ret);
				Integer rc = response.getInt("rc");
				if (rc == 0) {
					JSONObject responseData = new JSONObject(response.get("data"));
					if (responseData != null) {
						JSONArray responseDataTrends = new JSONArray(responseData.get("trends"));
						if (responseDataTrends != null) {
							trends = new double[responseDataTrends.size()];
							for(int i =0; i < responseDataTrends.size(); i++) {
								String itm  = responseDataTrends.get(i).toString();
								String[] itmSpt = itm.split(" ");
								String[] itmSenSpt =  itmSpt[1].split(",");
								trends[i] = Double.parseDouble(itmSenSpt[1]);
							}
						}
					}
				} else {
					log.info("从东财获取股票行情-"+indexName+"股票趋势数据失败，返回数据：{}", ret);
				}
			}
		} catch (Exception ex) {
			log.info("从东财获取股票行情-"+indexName+"股票趋势数据异常", ex);
		}
		log.info("====================从东财获取股票行情-"+indexName+"股票趋势数据结束====================");
		return trends;
	}
	
}
