package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import enums.StockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import vo.common.StockQuotesVO;

/**
 * qosapi股票数据
 */
@Slf4j
public class QosApi {
	
	private final static String qosApiKey = "0c93111d3f4f7d3aca870442dfbe2503";
	
	private final static String qosUrl = "https://api.qos.hk";
		
	/**
	 * 执行-qosapi-股票行情实时数据接口，获取数据
	 * @param stockGids = stockType+stockCode
	 * @return
	 */
	public static List<StockQuotesVO> doQosapiToGetFinanceStockRealtime(List<String> stockGids){
		List<String> codes  = qosapiCodesConvert(stockGids);
		return getSnapshot(codes);
	}
	
	/**
	 * qosapi交易品种列表数组，转换（取前两位股票类型，区分设置进不同的股票数组，取后面的内容，进行逗号分隔，）
	 * @param stockGids = stockType+stockCode
	 * @return
	 */
	public static List<String> qosapiCodesConvert(List<String> stockGids) {
		List<String> codes = new ArrayList<String>();
		String shCodes = "";
		String szCodes = "";
		String usCodes = "";
		String hkCodes = "";
		for(String object : stockGids) {
			if (!StringUtil.isEmpty(object)) {
				String stockType = object.substring(0,2);
				String stockCode = object.substring(2);
				StockTypeEnum stockTypeEnum = StockTypeEnum.getByCode(stockType);
				switch (stockTypeEnum) {
				case SH:
					shCodes += stockCode + ",";
					break;
				case SZ:
					szCodes += stockCode + ",";
					break;
				case US:
					usCodes += stockCode + ",";
					break;
				case HK:
					hkCodes += stockCode + ",";
					break;
				default:
					break;
				}
			}
		}
		if (shCodes.length() > 0) {
			shCodes = shCodes.substring(0,shCodes.length() -1);
			codes.add("SH:"+shCodes);
		}
		if (szCodes.length() > 0) {
			szCodes = szCodes.substring(0,szCodes.length() -1);
			codes.add("SZ:"+szCodes);
		}
		if (usCodes.length() > 0) {
			usCodes = usCodes.substring(0,usCodes.length() -1);
			codes.add("US:"+usCodes);
		}
		if (hkCodes.length() > 0) {
			hkCodes = hkCodes.substring(0,hkCodes.length() -1);
			codes.add("HK:"+hkCodes);
		}
		return codes;
	}
	
	/**
	 * qosapi-股票行情实时数据
	 * @param stockType 股票市场类型（sh：沪股，sz：深股，bj：北证，us：美股，hk：港股）
	 * @param stockCodes 股票代码，多个逗号分隔
	 * @return
	 */
	public static List<StockQuotesVO> getSnapshot(List<String> codes) {
		List<StockQuotesVO> list = new ArrayList<>();
		try {
			String snapshotUrl = qosUrl+"/snapshot";
			log.info("从qosapi-获取-股票行情实时数据地址：{}", snapshotUrl);
			//设置请求头
			Map<String,String> heads = new HashMap<String, String>();
			heads.put("key", qosApiKey);
			heads.put("Content-Type", "application/json;charset=UTF-8");
			//设置-默认请求body
			JSONObject json = new JSONObject();
			json.set("codes", codes);
			//发起请求
			String result = HttpRequest.sendPost(snapshotUrl, json.toString(), heads);
			if (StringUtil.isEmpty(result)) {
				log.info("从qosapi-获取-股票行情实时数据\""+codes+"\"无响应");
				return null;
			}
			JSONObject resultResponse = new JSONObject(result);
			String success = resultResponse.getStr("msg");
			if(success.equals("OK")) {
				Object dataObject = resultResponse.get("data");
				//判断data是list
				if (dataObject != null && dataObject instanceof JSONArray) {
					JSONArray resultResponseData = new JSONArray(dataObject);
					if (resultResponseData != null && resultResponseData.size() > 0) {
						for (int i=0; i<resultResponseData.size(); i++) {
							JSONObject job = resultResponseData.getJSONObject(i);
							if (job != null) {
								StockQuotesVO v = new StockQuotesVO();
								String c = job.getStr("c");
								String[] cArr = c.split(":");
								v.setGid(cArr[0].toLowerCase() + cArr[1]);
								//v.setStockName(;//无,前面业务需要存储名称
								BigDecimal percentageIncrease = BigDecimal.ZERO;//涨幅比
								BigDecimal nowPrice = job.getBigDecimal("lp");//当前价
								BigDecimal prevClose = job.getBigDecimal("yp");//昨日收盘价
								if (nowPrice.compareTo(BigDecimal.ZERO) != 0 && prevClose.compareTo(BigDecimal.ZERO) != 0) {
									percentageIncrease = (nowPrice).subtract(prevClose);//当前价-昨日收盘价
									percentageIncrease = percentageIncrease.multiply(new BigDecimal("100")).divide(prevClose, RoundingMode.DOWN);//（当前价-昨日收盘价）*100/收盘价
								}
								v.setPercentageIncrease(percentageIncrease);
								v.setNowPrice(nowPrice);
								v.setHeightPrice(job.getBigDecimal("h"));
								v.setLowPrice(job.getBigDecimal("l"));
								v.setOpenPrice(job.getBigDecimal("o"));
								v.setVolume(job.getBigDecimal("t"));
								v.setTurnover(job.getBigDecimal("v"));
								v.setPrevClose(prevClose);
								list.add(v);
							}
						}
					}
				}
			}else {
				log.info("从qosapi-获取-股票行情实时数据\""+codes+"\"失败,codes出错，返回数据：{}", result);
			}
		} catch (Exception e) {
			log.info("从qosapi-获取-股票行情实时数据\""+ codes +"\"异常", e);
		}
		return list;
	}
	

	
	
	
	public static void main(String[] args) {
		
		List<String> stockGids = new ArrayList<String>();
		stockGids.add("usACN");
		stockGids.add("hk00097");
		stockGids.add("bj430017");
		stockGids.add("sz001382");
		stockGids.add("usAAPL");
		System.out.println(stockGids);
		
		System.out.println(doQosapiToGetFinanceStockRealtime(stockGids));

	}
	
	
	

}
