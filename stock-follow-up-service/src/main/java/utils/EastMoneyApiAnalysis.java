package utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import entity.common.StockInfo;
import enums.StockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import vo.common.StockDayTransactionVO;
import vo.common.StockKChartVO;
import vo.common.StockQuotesVO;
import vo.server.StockData;

/**
 * 东财API解析
 */
@Slf4j
public class EastMoneyApiAnalysis {
	
	//东财-股票搜索url地址
	private final static String getStockSearchUrl = "http://search-codetable.eastmoney.com/codetable/search/web/wap?keyword=%s&label=%s&isHighLight=false";
	
	//东财-股票信息url地址
	private final static String getStockInfoUrl = "http://push2delay.eastmoney.com/api/qt/stock/get?secid=%S&fltt=2&forcect=1&invt=2&fields=f43,f44,f45,f46,f47,f48,f49,f55,f59,f60,f71,f84,f85,f92,f106,f116,f117,f152,f161,f164,f165,f167,f168,f169,f170,f172,f173,f324,f600,f602";
	
	//东财-股票分时曲线-数据url地址
	private final static String getStocktrendsUrl = "http://push2delay.eastmoney.com/api/qt/stock/trends2/get?secid=%s&fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14,f17&fields2=f51,f53,f54,f55,f56,f57,f58&iscr=0&iscca=0&ut=f057cbcbce2a86e2866ab8877db1d059&ndays=1";
	
	//东财-股票k线-数据url地址
	private final static String getStockKChartUrl = "http://push2his.eastmoney.com/api/qt/stock/kline/get?secid=%s&klt=%s&fqt=1&lmt=66&end=20500000&iscca=1&fields1=f1,f2,f3,f4,f5,f6,f7,f8&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f63,f64&ut=f057cbcbce2a86e2866ab8877db1d059&forcect=1";

	//东财-股票-列表数据（涨幅排序）url地址
	private final static String getStockQuoteListUrl = "http://push2delay.eastmoney.com/api/qt/clist/get?fs=%s&pn=%s&pz=%s&fid=f3&po=1&np=1&fltt=2&_=%s";
	
	//东财-分时成交-数据url地址
	private final static String getStockDayTransactionUrl = "https://push2delay.eastmoney.com/api/qt/stock/details/get?secid=%s&pos=-%s&fields1=f1,f2,f3,f4,f5&fields2=f51,f52,f53,f54,f55";
		
	public static class EastMoneyStockSearchReturn{
		
		Integer market;
		String shortName;
		
		public Integer getMarket() {
			return market;
		}
		public void setMarket(Integer market) {
			this.market = market;
		}
		public String getShortName() {
			return shortName;
		}
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
		
	}
	
	/**
	 * 东财-股票搜索-获取股票market信息
	 * @param stockType 股票类型
	 * @param stockCode 股票代码
	 * @return
	 */
	public static EastMoneyStockSearchReturn getEastMoneyStockSearch(String stockType, String stockCode) {
		EastMoneyStockSearchReturn eastMoneyStockSearchReturn = new EastMoneyStockSearchReturn();
		try {
			String stockSearchUrl;
			switch (stockType) {
			case "sh":
			case "bj":
			case "sz":
				stockSearchUrl = String.format(getStockSearchUrl, stockCode,"HSJ");
				break;
			default:
				stockSearchUrl = String.format(getStockSearchUrl, stockCode,"GM");
				break;
			}
			log.info("从东财获取-股票搜索地址：{}", stockSearchUrl);
			String stockSearchRet = HttpRequest.get(stockSearchUrl).execute().body();
			if (StringUtil.isEmpty(stockSearchRet)) {
				log.info("从东财获取-股票搜索\""+stockCode+"\"无响应");
				return null;
			}
			JSONObject stockSearchRetResponse = new JSONObject(stockSearchRet);
			Integer code = stockSearchRetResponse.getInt("code");
			if(code == 0) {
				JSONObject stockSearchRetResponseResult = new JSONObject(stockSearchRetResponse.get("result"));
				if (stockSearchRetResponseResult != null) {
					JSONArray stockSearchRetResponseResultQuoteList = new JSONArray(stockSearchRetResponseResult.get("quoteList"));
					if (stockSearchRetResponseResultQuoteList != null && stockSearchRetResponseResultQuoteList.size() > 0) {
						for (int i=0; i<stockSearchRetResponseResultQuoteList.size();i++) {
							JSONObject job = stockSearchRetResponseResultQuoteList.getJSONObject(i);
							if (job.get("code") != null && job.get("code").equals(stockCode) && job.get("market") != null) {
								Integer market = (Integer) job.get("market");
								log.info("从东财获取-股票搜索\""+stockCode+"\"完成，获取的market值：{}", market);
								eastMoneyStockSearchReturn.setMarket(market);
								if (job.get("shortName") != null) {
									String shortName = job.getStr("shortName");
									eastMoneyStockSearchReturn.setShortName(shortName);
								}
								return eastMoneyStockSearchReturn;
							}
						}
					}
				}
			}
			log.info("从东财获取-股票搜索\""+stockCode+"\"失败,code出错，返回数据：{}", stockSearchRet);
		} catch (Exception e) {
			log.info("从东财获取-股票搜索\""+stockCode+"\"异常", e);
		}
		return eastMoneyStockSearchReturn;
	}
	
	/**
	 * 东财-获取股票信息
	 * @param market
	 * @param stockCode 股票代码
	 * @return
	 */
	private static JSONObject getEastMoneyStockInfo(Integer market, String stockCode) {
		String stockInfoUrl;
		String secid = market+"."+stockCode;
		stockInfoUrl = String.format(getStockInfoUrl,secid);
		log.info("从东财获取-"+stockCode+"股票信息地址：{}",stockInfoUrl);
		String stockInfoRet = HttpRequest.get(stockInfoUrl).execute().body();
		if (StringUtil.isEmpty(stockInfoRet)) {
			log.info("从东财获取-"+stockCode+"股票信息无响应");
			return null;
		} 
		JSONObject stockInfoResponse = new JSONObject(stockInfoRet);
		Integer rc = stockInfoResponse.getInt("rc");
		if (rc == 0) {
			JSONObject data = new JSONObject(stockInfoResponse.get("data"));
			if (data != null) {
				return data;
			}
		}
		log.info("从东财获取-"+stockCode+"股票信息失败，返回数据：{}",stockInfoRet);
		return null;
	}

	/**
	 * 一手的股数
	 * @param stockType
	 * @param stockCode
	 * @return
	 * @throws Exception 
	 */
	public static Integer getSharesOfHand(String stockType, String stockCode){
		//1、通过股票类型，股票代码，搜索股票，获取market
		EastMoneyStockSearchReturn eastMoneyStockSearchReturn = getEastMoneyStockSearch(stockType, stockCode);
		if (eastMoneyStockSearchReturn.getMarket() != null) {
			//2、通过market，股票代码	，获取股票信息Json对象	
			JSONObject data = getEastMoneyStockInfo(eastMoneyStockSearchReturn.getMarket(), stockCode);
			if (data.get("f602") != null) {
				return (Integer) data.get("f602");
			}
		}
		return null;
	}
	
	/**
	 * 获取股票k线图
	 * @param stockType 股票代码
	 * @param stockCode 股票类型
	 * @param candlestickChartType K线图类型：(1-日K，2-周K，3-月K)
	 * @return
	 */
	public static StockKChartVO getStockKChart(String stockType, String stockCode, Integer candlestickChartType) {
		//1、通过股票类型，股票代码，搜索股票，获取market
		EastMoneyStockSearchReturn eastMoneyStockSearchReturn = EastMoneyApiAnalysis.getEastMoneyStockSearch(stockType, stockCode);
		if (eastMoneyStockSearchReturn.getMarket() != null) {
			return getStockTrendsOrklinesData(eastMoneyStockSearchReturn.getMarket(),stockCode,candlestickChartType);
		}
		return null;
	}
	
	/**
	 * 获取-东财-股票分时/k线数据
	 * @param market
	 * @param stockCode
	 * @return
	 */
	private static StockKChartVO getStockTrendsOrklinesData(Integer market, String stockCode ,Integer candlestickChartType) {
		// TODO Auto-generated method stub
		String stockKChartUrl;
		String secid = market+"."+stockCode;
		switch (candlestickChartType) {
		case 0:
			stockKChartUrl = String.format(getStocktrendsUrl,secid);//分时
			break;
		case 1:
			stockKChartUrl = String.format(getStockKChartUrl, secid,"101");//日K
			break;
		case 2:
			stockKChartUrl = String.format(getStockKChartUrl, secid,"102");//周K
			break;
		default:
			stockKChartUrl = String.format(getStockKChartUrl, secid,"103");//月K
			break;
		}
		log.info("从东财获取-股票分时/k线数据地址：{}", stockKChartUrl);
		String stockKChartRet = HttpRequest.get(stockKChartUrl).execute().body();
		if (StringUtil.isEmpty(stockKChartRet)) {
			log.info("从东财获取-"+stockCode+"股票分时/k线数据无响应");
			return null;
		} 
		JSONObject stockKChartResponse = new JSONObject(stockKChartRet);
		Integer rc = stockKChartResponse.getInt("rc");
		if (rc == 0) {
			JSONObject stockKChartResponseData = new JSONObject(stockKChartResponse.get("data"));
			if (stockKChartResponseData != null) {
				JSONArray stockKChartResponseDataKlines = null;
				if (candlestickChartType == 0) {//分时-趋势图
					stockKChartResponseDataKlines = new JSONArray(stockKChartResponseData.get("trends"));
				}else {//日/周/月-k图
					stockKChartResponseDataKlines = new JSONArray(stockKChartResponseData.get("klines"));
				}
				if (stockKChartResponseDataKlines != null) {
					String[] candlestickChart = stockKChartResponseDataKlines.toArray(new String[stockKChartResponseDataKlines.size()]);
					StockKChartVO stockKChartVO = new StockKChartVO();
					stockKChartVO.setCandlestickChart(candlestickChart);
					return stockKChartVO;
				}
			}
		}
		log.info("从东财获取-"+stockCode+"股票分时/k线数据失败，返回数据：{}",stockKChartRet);
		return null;
	}

	/**
	 * 获取-东财股票-实时行情
	 * @param stockType
	 * @param stockCode
	 * @return
	 * @throws Exception 
	 */
	public static StockQuotesVO getStockRealTimeData(String stockType, String stockCode){
		StockQuotesVO stockQuotesVO = new StockQuotesVO();
		EastMoneyStockSearchReturn eastMoneyStockSearchReturn = getEastMoneyStockSearch(stockType, stockCode);
		if (eastMoneyStockSearchReturn.getMarket() != null) {
			//2、通过market，股票代码	，获取股票信息Json对象	
			JSONObject data = getEastMoneyStockInfo(eastMoneyStockSearchReturn.getMarket(), stockCode);
			//System.out.println(data);
			stockQuotesVO.setGid(stockType + stockCode);
			stockQuotesVO.setStockName(eastMoneyStockSearchReturn.getShortName());
			stockQuotesVO.setPercentageIncrease(data.getBigDecimal("f170"));
			stockQuotesVO.setNowPrice(data.getBigDecimal("f43"));
			stockQuotesVO.setHeightPrice(data.getBigDecimal("f44")); 
			stockQuotesVO.setLowPrice(data.getBigDecimal("f45")); 
			stockQuotesVO.setOpenPrice(data.getBigDecimal("f46"));
			stockQuotesVO.setVolume(data.getBigDecimal("f47"));
			stockQuotesVO.setTurnover(data.getBigDecimal("f48"));
			stockQuotesVO.setPrevClose(data.getBigDecimal("f60"));
			stockQuotesVO.setSharesOfHand(data.getInt("f602"));
			stockQuotesVO.setMarket(eastMoneyStockSearchReturn.getMarket());
		}
		return stockQuotesVO;
	}
	
	/**
	 * 获取-东财-股票-列表数据（涨幅排序）
	 * @param stockType 股票类型，可选择为（sh：沪股、sz：深股、bj：北证、us：美股、hk：港股），为空则处理所有A股
	 * @return
	 */
	public static List<?> getStockList(String stockType,Class classObject){
		//创建空股票list
		List<Object> stockQuoteList = new ArrayList<>();
		//设定查询条件分页值
		String fs = "m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048";//默认所有A股
		int pn = 1;//第几页
		int pz = 100;//最多只能查100条
		int total = 0;//总数
		//判断股市
		String markName = "A股";
		StockTypeEnum stockTypeEnum = StockTypeEnum.getByCode(stockType);
		if (stockTypeEnum != null) {
			markName = stockTypeEnum.getName();
			switch (stockTypeEnum) {
				case SH:
					fs = "m:1+t:2,m:1+t:23";//沪股
					break;
				case SZ:
					fs = "m:0+t:6,m:0+t:80";//深股
					break;
				case BJ:
					fs = "m:0+t:81+s:2048";//北证
					break;
				case US:
					fs = "m:105,m:106,m:107";//美股
					break;
				case HK:
					fs = "m:128+t:3,m:128+t:4,m:128+t:1,m:128+t:2";//港股
					break;
				default:
					break;
			}
		}
		//循环发起请求
		do {
			try {
				Thread.currentThread().sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//设置分页请求地址
			String url = String.format(getStockQuoteListUrl, fs, pn, pz,System.currentTimeMillis());
			log.info("从东财获取"+markName+"列表数据（涨幅排序）url地址：{}",url);
			//发起请求
			String ret =  HttpClientRequest.doGet(url);
			//System.out.println(ret);
			if(StringUtil.isEmpty(ret)) {
				log.info("====================从东财获取"+markName+"列表数据（涨幅排序）无响应====================");
				return null;
			}
			JSONObject json = new JSONObject(ret);
			JSONObject data = json.getJSONObject("data");
			total = data.getInt("total");
			JSONArray diff = data.getJSONArray("diff");
			for(Object o : diff) {
				JSONObject i = new JSONObject(o);
				//获取股票数据，进行转换
				Object obj = getObject(i,stockTypeEnum,classObject);
				//股票添加进股票list
				stockQuoteList.add(obj);
			}
		} while(pn++ * pz < total);
		//返回股票list
		return stockQuoteList;
	}

	/**
	 * 获取股票数据，进行转换
	 * @param i
	 * @param stockTypeEnum
	 * @param classObject
	 * @return
	 */
	private static Object getObject(JSONObject i, StockTypeEnum stockTypeEnum ,Class classObject) {
		// TODO Auto-generated method stub
		String stockCode = i.getStr("f12");
		String stockName = i.getStr("f14");
		String stockType = "";
		if (stockTypeEnum != null) {
			stockType = stockTypeEnum.getCode();
		}else {//A股所有数据
			String codeStartWith = stockCode.substring(0, 1);
			switch(codeStartWith) {
			case "6":
				stockType = StockTypeEnum.SH.getCode();
				break;
			case "0":
			case "3":
				stockType = StockTypeEnum.SZ.getCode();
				break;
			default:
				stockType = StockTypeEnum.BJ.getCode();
				break;
			}
		}
		//返回stockData
		if (classObject.equals(StockData.class)) {
			StockData sd = new StockData();
			//设置通用股票数据
			sd.setStockCode(stockCode);
			sd.setStockName(stockName);
			sd.setStockType(stockType);
			sd.setNowPrice(new BigDecimal(!Objects.equals(i.getStr("f2"), "-") ? i.getStr("f2"):"0"));
			sd.setPrevClose(new BigDecimal(!Objects.equals(i.getStr("f18"), "-") ? i.getStr("f18"):"0"));
			sd.setTurnover(new BigDecimal(!Objects.equals(i.getStr("f5"), "-") ? i.getStr("f5"):"0"));
			return sd;
		}else if(classObject.equals(StockInfo.class)) {//返回stockInfo
			StockInfo stockInfo = new StockInfo();
			stockInfo.setStockCode(stockCode);
			stockInfo.setStockName(stockName);
			stockInfo.setStockType(stockType);
			return stockInfo;
		}
		return null;
	}
	
	/**
	 * 获取股票-分时成交
	 * @param stockType
	 * @param stockCode
	 * @return
	 */
	public static StockDayTransactionVO getStockDayTransaction(String stockType, String stockCode) {
		
		//1、通过股票类型，股票代码，搜索股票，获取股票信息，与market值
		StockQuotesVO stockQuotesVO = getStockRealTimeData(stockType, stockCode);
		if (stockQuotesVO.getMarket() != null) {
			StockDayTransactionVO stockDayTransactionVO = new StockDayTransactionVO();
			stockDayTransactionVO.setStockType(stockType);
			stockDayTransactionVO.setStockCode(stockCode);
			stockDayTransactionVO.setDataGetTime(new Date());
			stockDayTransactionVO.setStockName(stockQuotesVO.getStockName());
			stockDayTransactionVO.setSharesOfHand(stockQuotesVO.getSharesOfHand());
			String secid = stockQuotesVO.getMarket()+"."+stockCode;
			String stockDayTransactionUrl = String.format(getStockDayTransactionUrl,secid,100000);//此处10000是最数据量，如果数据量超过10万，则只能查到10万，特殊情况，在看如何特殊处理
			log.info("从东财获取-股票分时成交-数据地址：{}", stockDayTransactionUrl);
			String ret = HttpRequest.get(stockDayTransactionUrl).execute().body();
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取-"+stockCode+"股票股票分时成交数据无响应");
				return null;
			} 
			JSONObject response = new JSONObject(ret);
			Integer rc = response.getInt("rc");
			if (rc == 0) {
				JSONObject responseData = new JSONObject(response.get("data"));
				if (responseData != null) {
					JSONArray details = new JSONArray(responseData.get("details"));
					if (details != null) {
						stockDayTransactionVO.setDetails(details);
					}
				}
			}
			return stockDayTransactionVO;
		}
		return null;
	}

	public static void main(String[] args) {
//		for (int i=0; i<500; i++) {
//			System.out.println(DateUtil.formatTime(new Date())+","+ getStockTrendsOrklinesData(1,"000001",1));
//		}
		//System.out.println(getStockList("bj",StockInfo.class));
		//System.out.println(getEastMoneyStockSearch("us","NLSPW"));
		//System.out.println(getStockDayTransaction("us", "AAPL"));
		System.out.println(getEastMoneyStockSearch("us", "AAPL"));
		//System.out.println(getEastMoneyStockSearch("us", "NLSPW").getShortName());
		//System.out.println(getStockList("us"));
		//System.out.println(getStockRealTimeData("hk","08340"));
	}

}
