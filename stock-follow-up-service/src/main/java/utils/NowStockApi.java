package utils;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import entity.common.StockInfo;
import enums.StockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import vo.common.StockQuotesVO;

/**
 * nowapi股票数据
 */
@Slf4j
public class NowStockApi {
	
	private final static String nowapiAppkey = "74784";
	
	private final static String nowapiSign = "dd8f43ab5b04c0bd29d7cfd07720b0da";
	
	//nowapi-股票列表数据地址
	private final static String getFinanceStockListUrl = "http://api.k780.com/?app=finance.stock_list&category=%s&appkey=%s&sign=%s&format=json";

	
	//nowapi-股票行情实时数据地址
	private final static String getFinanceStockRealtimeUrl = "http://api.k780.com/?app=finance.stock_realtime&stoSym=%s&appkey=%s&sign=%s&format=json";
	
	
	/**
	 * 执行-nowapi-股票行情实时数据接口，获取数据
	 * @param stockGids = stockType+stockCode
	 * @return
	 */
	public static List<StockQuotesVO> doNowapiToGetFinanceStockRealtime(List<String> stockGids){
		String nowapiStoSym = nowapiStoSymConvert(stockGids);
		return getFinanceStockRealtime(nowapiStoSym);
	}
	
	/**
	 * nowapi股票代码转换（美股去除us，改为gb_后面的股票代码改为小写）进行逗号分隔
	 * @param stockGids = stockType+stockCode
	 * @return
	 */
	public static String nowapiStoSymConvert(List<String> stockGids) {
		String nowapiStoSym = "";
		for (String object : stockGids) {
			if (!StringUtil.isEmpty(object)) {
				if (object.substring(0, 2).equals("us")) {
					object = "gb_" + object.substring(2).toLowerCase();
				}
				nowapiStoSym += object + ",";
			}
		}
		if (nowapiStoSym.length() > 0) {
			nowapiStoSym = nowapiStoSym.substring(0, nowapiStoSym.length() - 1);
		}
		return nowapiStoSym;
	}
	
	/**
	 * nowapi-股票行情实时数据
	 * @param nowapiStoSym （nowapi股票代码,美股，gb_+股票代码，其他股，都是股票类型+股票代码）
	 * nowapi股票代码,多个逗号隔开,单次查询最多5000个,查看支持股票代码列表,
	 * 说明:1.单次查询10个或不足10个品种扣1次量。(例:单次查1个或10个扣1次量，查11个扣2次量)。
	 * 2.单次查询超500个建议POST方式。
	 * @return
	 */
	public static List<StockQuotesVO> getFinanceStockRealtime(String nowapiStoSym) {
		List<StockQuotesVO> list = new ArrayList<>();
		try {
			String financeStockRealtimeUrl = String.format(getFinanceStockRealtimeUrl, nowapiStoSym,nowapiAppkey,nowapiSign);
			
			log.info("从nowapi-获取-股票行情实时数据地址：{}", financeStockRealtimeUrl);
			String financeStockRealtimeRet = HttpRequest.get(financeStockRealtimeUrl).execute().body();
			if (StringUtil.isEmpty(financeStockRealtimeRet)) {
				log.info("从nowapi-获取-股票行情实时数据\""+nowapiStoSym+"\"无响应");
				return null;
			}
			System.err.println(financeStockRealtimeRet);
			JSONObject financeStockRealtimeRetResponse = new JSONObject(financeStockRealtimeRet);
			
			Integer success = financeStockRealtimeRetResponse.getInt("success");
			if(success == 1) {
				JSONObject financeStockRealtimeRetResponseResult = new JSONObject(financeStockRealtimeRetResponse.get("result"));
				if (financeStockRealtimeRetResponseResult != null) {
					JSONObject financeStockRealtimeRetResponseResultLists = new JSONObject(financeStockRealtimeRetResponseResult.get("lists"));
					if (financeStockRealtimeRetResponseResultLists != null ) {
						String[] nowapiStoSymArr = nowapiStoSym.split(",");
						for(String a : nowapiStoSymArr) {
							JSONObject job = financeStockRealtimeRetResponseResultLists.getJSONObject(a);
							if (job != null) {
								StockQuotesVO v = new StockQuotesVO();
								String symbol = job.getStr("symbol");
								String stockType = symbol.startsWith("hk") ? "hk" : "us";
								v.setGid(stockType + job.getStr("scode"));
								v.setStockName(job.getStr("sname"));
								v.setPercentageIncrease(job.getBigDecimal("rise_fall_per"));
								v.setNowPrice(job.getBigDecimal("last_price"));
								v.setHeightPrice(job.getBigDecimal("high_price"));
								v.setLowPrice(job.getBigDecimal("low_price"));
								v.setOpenPrice(job.getBigDecimal("open_price"));
								v.setVolume(job.getBigDecimal("turn_volume"));
								v.setTurnover(job.getBigDecimal("volume"));
								v.setPrevClose(job.getBigDecimal("yesy_price"));
								list.add(v);
							}
						}
					}
				}
			}else {
				log.info("从nowapi-获取-股票行情实时数据\""+nowapiStoSym+"\"失败,nowapiStoSym出错，返回数据：{}", financeStockRealtimeRet);
			}
		} catch (Exception e) {
			log.info("从nowapi-获取-股票行情实时数据\""+nowapiStoSym+"\"异常", e);
		}
		return list;
	}
	
	
	/**
	 * nowapi-股票列表数据
	 * @param category : 类别 hs:沪深北股市 hk:香港股市 us:美国股市
	 * @return
	 */
	public static List<StockInfo> getFinanceStockList(String stockType) {
		List<StockInfo> stockInfoList = new ArrayList<StockInfo>();	
		String category = "hs";//默认所有A股
		try {
			StockTypeEnum stockTypeEnum = StockTypeEnum.getByCode(stockType);
			if (stockTypeEnum != null) {
				switch (stockTypeEnum) {
				case US:
					category = "us";//美股
					break;
				case HK:
					category = "hk";//港股
					break;
				default:
					return stockInfoList;
				}
			}
			String financeStockListUrl = String.format(getFinanceStockListUrl, category,nowapiAppkey,nowapiSign);
			
			log.info("从nowapi-获取-股票行情实时数据地址：{}", financeStockListUrl);
			String financeStockListRet = HttpRequest.get(financeStockListUrl).execute().body();
			if (StringUtil.isEmpty(financeStockListRet)) {
				log.info("从nowapi-获取-股票列表数据\""+category+"\"无响应");
				return null;
			}
			JSONObject financeStockListRetResponse = new JSONObject(financeStockListRet);
			Integer success = financeStockListRetResponse.getInt("success");
			if(success == 1) {
				JSONObject financeStockListRetResponseResult = new JSONObject(financeStockListRetResponse.get("result"));
				if (financeStockListRetResponseResult != null) {
					JSONArray financeStockListRetResponseResultLists = new JSONArray(financeStockListRetResponseResult.get("lists"));
					log.info("从nowapi-获取-股票列表数据\""+category+"\"返回数据：{}", financeStockListRetResponseResultLists);
					if (financeStockListRetResponseResultLists != null && financeStockListRetResponseResultLists.size() > 0) {
						for (int i = 0; i < financeStockListRetResponseResultLists.size(); i++) {
							String symbol = financeStockListRetResponseResultLists.getJSONObject(i).getStr("symbol");
							String stockName = financeStockListRetResponseResultLists.getJSONObject(i).getStr("sname");
							String stockTypeValue = symbol.substring(0,2);
							String stockCode = symbol.substring(2);
							if (symbol.substring(0, 3).equals("gb_")) {
								stockTypeValue = StockTypeEnum.US.getCode();
								stockCode = symbol.substring(3).toUpperCase();
							}
							StockInfo stockInfo = new StockInfo();
							stockInfo.setStockType(stockTypeValue);
							stockInfo.setStockCode(stockCode);
							stockInfo.setStockName(stockName);
							stockInfoList.add(stockInfo);
						}
						return stockInfoList;
					}
				}
			}else {
				log.info("从nowapi-获取-股票列表数据\""+category+"\"失败,success出错，返回数据：{}", financeStockListRetResponse);
			}
		} catch (Exception e) {
			log.info("从nowapi-获取-股票列表数据\""+category+"\"异常", e);
		}
		return stockInfoList;
	}
	
		

	

	

	
	public static void main(String[] args) {

		
//		List<String> stockGids = new ArrayList<String>();
//		stockGids.add("usACN");
//		stockGids.add("hk00097");
//		stockGids.add("bj430017");
//		stockGids.add("sz001382");
//		stockGids.add("usAAPL");
//		System.out.println(stockGids);
//		
//		System.out.println(doNowapiToGetFinanceStockRealtime(stockGids));
		
//		String nowapiStoSym = nowapiStoSymConvert(stockGids);
//		System.out.println(nowapiStoSym);
//		System.out.println(getFinanceStockRealtime(nowapiStoSym));
		
//		System.out.println(getFinanceStockList("us"));
		
	}

}
