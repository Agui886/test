package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.hutool.http.HttpRequest;
import enums.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;
import vo.common.StockQuotesVO;
import vo.manager.MarketVO;

@Slf4j
@Service
public class SinaApi {

	private static String sinaMarketUrl = "https://hq.sinajs.cn/rn=1520407404627&list=s_sh000001,s_sz399001,s_sz399006,s_sz399300,s_sz399005,s_sz399673,s_sz399106,s_sz399004,s_sz399100";
	private static String sinaSingleExchangeUrl = "https://hq.sinajs.cn/list=";
	private static String sinaSingleStockUrl = "https://hq.sinajs.cn/list=";

	/**
	 * //查询 股票指数、大盘指数信息 返回示例： var
	 * hq_str_s_sh000001="上证指数,3271.9813,5.7435,0.18,6324881,67721487"; var
	 * hq_str_s_sz399001="深证成指,10584.42,53.569,0.51,880128107,103939719"; var
	 * hq_str_s_sz399006="创业板指,2164.66,13.144,0.61,40607208,13254635"; var
	 * hq_str_s_sz399300="沪深300,3882.48,-6.973,-0.18,248940196,41362549"; var
	 * hq_str_s_sz399005="中小100,6555.79,-11.543,-0.18,59050304,9527151"; var
	 * hq_str_s_sz399673="创业板50,2167.32,18.238,0.85,32213033,11418196"; var
	 * hq_str_s_sz399106="深证综指,1990.16,16.544,0.84,880128107,103939719"; var
	 * hq_str_s_sz399004="深证100R,6494.82,-1.753,-0.03,112265104,19368633"; var
	 * hq_str_s_sz399100="新指数,9407.92,80.889,0.87,856962013,102844740";
	 * 
	 * @return
	 */
	public static List<MarketVO> getMarket() {
		List<MarketVO> list = new ArrayList<>();
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
			headers.put("Referer",
					"https://quotes.sina.cn/hs/company/quotes/view/sz399001?vt=4&cid=76524&node_id=76524&autocallup=no&isfromsina=yes");
			headers.put("Cookie", "xq_a_token=d269ad4aee7ece063038900846f9541a7d0ead07");
			String ret = HttpRequest.get(sinaMarketUrl).addHeaders(headers).execute().body();
			log.info("查询新浪股票指数、大盘指数信息,返回\n{}", ret);
			String[] marketArray = ret.split(";");
			for (String market : marketArray) {
				if (StringUtils.isNotBlank(market)) {
					market = market.substring(market.indexOf("\"") + 1, market.lastIndexOf("\""));
					MarketVO marketVO = new MarketVO();
					String[] sh01_arr = market.split(",");
					marketVO.setName(sh01_arr[0]);
					marketVO.setNowPrice(sh01_arr[1]);
					marketVO.setIncrease(sh01_arr[2]);
					marketVO.setIncreaseRate(sh01_arr[3]);
					list.add(marketVO);
				}
			}
		} catch (Exception ex) {
			log.error("查询新浪股票指数、大盘指数信息, 异常", ex);
		}
		return list;
	}

	/**
	 * 查询单支股票信息
	 * 
	 * @param stockType
	 * @param stockCode
	 * @return
	 */
	public static StockQuotesVO getSinaStock(String stockType, String stockCode) {
		String result = "";
		try {
			result = HttpClientRequest.doGet(sinaSingleStockUrl + stockType + stockCode);
			log.info(result);
			StockQuotesVO ret = assembleStockQuotesVO(result);
			ret.setGid(stockType + stockCode);
			return ret;
		} catch (Exception e) {
			log.error("获取新浪股票行情出错，错误信息 = {}", e);
		}
		return new StockQuotesVO();
	}

	/**
	 * 查询多支股票信息
	 * 
	 * @param stockGids=stockType+stockCode
	 * @return
	 */
	public static List<StockQuotesVO> getSinaStocks(List<String> stockGids) {
		List<StockQuotesVO> list = new ArrayList<>();
		if (stockGids.size() == 0)
			return list;
		StringBuilder sb = new StringBuilder(stockGids.get(0));
		if (stockGids.size() > 1) {
			for (int i = 1; i < stockGids.size(); i++) {
				sb.append(",").append(stockGids.get(i));
			}
		}
		try {
			String result = HttpClientRequest.doGet(sinaSingleStockUrl + sb);
			log.info("获取新浪股票行情返回 = {}", result);
			String[] resultArr = result.split(";");
			for (String a : resultArr) {
				if(StringUtil.isEmpty(a) || a.equals("\n")) {
					continue;
				}
				StockQuotesVO v = assembleStockQuotesVO(a);
				stockGids.forEach(i -> {
					if (a.contains(i)) {
						v.setGid(i);
					}
				});
				list.add(v);
			}
		} catch (Exception e) {
			log.error("获取新浪股票行情出错，错误信息 = {}", e);
		}
		return list;
	}

	/**
	 * 获取人民币兑换外币汇率
	 * @param currency
	 */
	public static BigDecimal getExchangeRate(CurrencyEnum currency) {
		String gid;
		switch(currency) {
		case HKD:
			gid = "fx_scnyhkd";
			break;
		case USD:
			gid = "fx_scnyusd";
			break;
		default:
			gid = "fx_scnycny";
			break;
		}
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
			headers.put("Referer",
					"https://quotes.sina.cn/hs/company/quotes/view/sz399001?vt=4&cid=76524&node_id=76524&autocallup=no&isfromsina=yes");
			headers.put("Cookie", "xq_a_token=d269ad4aee7ece063038900846f9541a7d0ead07");
			String result = HttpRequest.get(sinaSingleExchangeUrl + gid).addHeaders(headers).execute().body();
			log.info("获取新浪人民币兑换外币汇率 = {}", result);
			if(StringUtil.isEmpty(result)) {
				return null;
			}
			result = result.substring(result.indexOf("\"") + 1, result.lastIndexOf("\""));
			if (!result.contains(",")) {
				return null;
			}
			return new BigDecimal(result.split(",")[8]);
		} catch (Exception e) {
			log.error("获取新浪人民币兑换外币汇率出错，错误信息 = {}", e);
			return null;
		}
	}

	private static StockQuotesVO assembleStockQuotesVO(String sinaResult) {
		StockQuotesVO ret = new StockQuotesVO();
		boolean isHkOrUs = sinaResult.contains("str_hk") || sinaResult.contains("str_us");
		if (isHkOrUs) {
			// 港股和美股的数据不准确
			return ret;
		}
		sinaResult = sinaResult.substring(sinaResult.indexOf("=") + 2);
		String[] hqarr = sinaResult.split(",");
		if (hqarr.length > 1) {
			ret.setStockName(hqarr[0]);
			ret.setNowPrice(new BigDecimal(hqarr[3]));
			BigDecimal chang_rate = BigDecimal.ZERO;
			BigDecimal hqarr2 = new BigDecimal(hqarr[2]);
			BigDecimal hqarr3 = new BigDecimal(hqarr[3]);
			if (hqarr2.compareTo(BigDecimal.ZERO) != 0 && hqarr3.compareTo(BigDecimal.ZERO) != 0) {
				chang_rate = (hqarr3).subtract(hqarr2);
				chang_rate = chang_rate.multiply(new BigDecimal("100")).divide(hqarr2, RoundingMode.DOWN);
			}
			ret.setPercentageIncrease(chang_rate);
			ret.setHeightPrice(new BigDecimal(hqarr[4]));
			ret.setLowPrice(new BigDecimal(hqarr[5]));
			BigDecimal volume = new BigDecimal(hqarr[8]);
			ret.setVolume(volume.divide(new BigDecimal("100"),RoundingMode.DOWN));
			ret.setTurnover(new BigDecimal(hqarr[9]));
			ret.setPrevClose(hqarr2);
			ret.setOpenPrice(new BigDecimal(hqarr[1]));
		}
		return ret;
	}

	public static void main(String[] args) {
		getExchangeRate(CurrencyEnum.USD);
		getSinaStock("sz", "002261");
	}
}
