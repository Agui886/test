package utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import entity.common.StockCompanyInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EastMoneyApi {
	
	private final static String getUsStockCompanyUrl = "https://datacenter.eastmoney.com/securities/api/data/v1/get?reportName=RPT_USF10_INFO_ORGPROFILE&columns=SECUCODE%2CSECURITY_CODE%2CORG_CODE%2CSECURITY_INNER_CODE%2CORG_NAME%2CORG_EN_ABBR%2CBELONG_INDUSTRY%2CFOUND_DATE%2CCHAIRMAN%2CREG_PLACE%2CADDRESS%2CEMP_NUM%2CORG_TEL%2CORG_FAX%2CORG_EMAIL%2CORG_WEB%2CORG_PROFILE&filter=(SECUCODE%3D%22@@@@%22)&pageNumber=1&pageSize=1&v=04330847544798817";
	
	private final static String getHkStockCompanyUrl = "https://datacenter.eastmoney.com/securities/api/data/v1/get?reportName=RPT_HKF10_INFO_ORGPROFILE&columns=SECUCODE%2CSECURITY_CODE%2CSECURITY_NAME_ABBR%2CORG_NAME%2CORG_EN_ABBR%2CHK_SHARES%2CBELONG_INDUSTRY%2CFOUND_DATE%2CREG_CAPITAL%2CCHAIRMAN%2CSECRETARY%2CMAIN_BUSINESS%2CEMP_NUM%2CREG_ADDRESS%2CADDRESS%2CSHARES_REG_ADDRESS%2CACCOUNT_FIRM%2CMAIN_RELATED_BANK%2CLEGAL_ADVISER%2CORG_WEB%2CORG_EMAIL%2CORG_TEL%2CORG_FAX&filter=(SECUCODE%3D%22@@@@%22)&pageNumber=1&pageSize=100&v=011531244394660423";
	
	private final static String getAStockCompanyUrl = "https://datacenter.eastmoney.com/securities/api/data/get?type=RPT_F10_ORG_BASICINFO&sty=SECUCODE%2CSECURITY_CODE%2CSECURITY_NAME_ABBR%2CORG_NAME%2CFORMERNAME%2CSTR_CODEH%2CSTR_NAMEH%2CSTR_CODEA%2CSTR_NAMEA%2CSTR_CODEB%2CSTR_NAMEB%2CREGIONBK%2CEM2016%2CBLGAINIAN%2CCHAIRMAN%2CLEGAL_PERSON%2CPRESIDENT%2CSECRETARY%2CFOUND_DATE%2CREG_CAPITAL%2CTOTAL_NUM%2CTATOLNUMBER%2CORG_TEL%2CORG_EMAIL%2CORG_WEB%2CADDRESS%2CREG_ADDRESS%2CORG_PROFIE%2CMAIN_BUSINESS%2CSECURITY_TYPE_CODE%2CCURRENCY%2CACCOUNT_FIRM%2CLEGAL_ADVISER%2CEXPAND_NAME_ABBR%2CORG_PROFILE&filter=(SECUCODE%3D%22@@@@%22)&client=APP&source=SECURITIES&rdm=rnd_D37CC619510A45838ADB11D500CC8964&v=008164038941729634";
	
	
	public static StockCompanyInfo getStockCompanyInfo(String stockType, String stockCode) {
		try {
			String url;
			
			switch (stockType) {
			case "sh":
			case "bj":
			case "sz":
				url = getAStockCompanyUrl.replace("@@@@", stockCode + "." + stockType.toUpperCase());
				break;
			case "hk":
				url = getHkStockCompanyUrl.replace("@@@@", stockCode + "." + stockType.toUpperCase());
				break;
			default:
				url = getUsStockCompanyUrl.replace("@@@@", stockCode + ".O");
				break;
			}
			log.info("从东财获取公司信息地址：{}", url);
			String ret = HttpRequest.get(url).execute().body();
			if (StringUtil.isEmpty(ret)) {
				log.info("从东财获取公司信息无响应");
				return null;
			}
			log.info("从东财获取公司信息返回：{}", ret);
			JSONObject response = new JSONObject(ret);
			Integer code = response.getInt("code");
			if(code != 0) {
				if(stockType.equals("us")) {
					url = getUsStockCompanyUrl.replace("@@@@", stockCode + ".N");
					log.info("重新从东财获取美股公司信息地址：{}", url);
					ret = HttpRequest.get(url).execute().body();
					if (StringUtil.isEmpty(ret)) {
						log.info("重新从东财获取美股公司信息无响应");
						return null;
					}
					log.info("重新从东财获取美股公司信息返回：{}", ret);
					response = new JSONObject(ret);
				}
			}
			JSONArray dataList = response.getJSONObject("result").getJSONArray("data");
			JSONObject data = dataList.getJSONObject(0);
			StockCompanyInfo s = new StockCompanyInfo();
			s.setStockType(stockType);
			s.setStockCode(stockCode);
			s.setOrgName(data.getStr("ORG_NAME"));
			s.setOrgEnAbbr(data.getStr("ORG_EN_ABBR"));
			s.setBelongIndustry(data.getStr("BELONG_INDUSTRY"));
			s.setChairman(data.getStr("CHAIRMAN"));
			s.setFoundDate(data.getStr("FOUND_DATE"));
			s.setEmpNum(data.getStr("EMP_NUM"));
			s.setRegPlace(data.getStr("REG_PLACE"));
			s.setAddress(data.getStr("ADDRESS"));
			s.setOrgWeb(data.getStr("ORG_WEB"));
			s.setOrgMail(data.getStr("ORG_MAIL"));
			s.setOrgTel(data.getStr("ORG_TEL"));
			s.setOrgFax(data.getStr("ORG_FAX"));
			s.setOrgProfile(data.getStr("ORG_PROFILE"));
			return s;
		} catch(Exception ex) {
			log.info("从东财获取公司信息异常", ex);
			return null;
		}
	}
	
	 //hk us 数据转换
//    public static StockQuotesVO getStockQuotesVO(String stockType, String stockCode) {
//    	String result;
//    	RedisDao redisDao = ApplicationContextProvider.getBean(RedisDao.class);
//    	switch(stockType) {
//    	case Constant.STOCK_TYPE_US:
//    		result = redisDao.getStr(RedisDbTypeEnum.STOCK_US, stockType + stockCode);
//    		break;
//    	case Constant.STOCK_TYPE_HK:
//    		result = redisDao.getStr(RedisDbTypeEnum.STOCK_HK, stockType + stockCode);
//    		break;
//    	default:
//    		result = "";
//    		break;
//    	}
//    	StockQuotesVO ret = new StockQuotesVO();
//    	if(StringUtil.isEmpty(result)) {
//    		ret.setNowPrice(BigDecimal.ZERO);
//	        ret.setPercentageIncrease(BigDecimal.ZERO);
//	        ret.setHeightPrice(BigDecimal.ZERO);
//	        ret.setLowPrice(BigDecimal.ZERO);
//	        ret.setVolume(BigDecimal.ZERO);
//	        ret.setTurnover(BigDecimal.ZERO);
//	        ret.setPrevClose(BigDecimal.ZERO);
//    	} else {
//	        JSONObject jsonObject = new JSONObject(result);
//	        ret.setStockName(jsonObject.getStr("f14"));
//	        ret.setPercentageIncrease(new BigDecimal(!Objects.equals(jsonObject.getStr("f3"), "-") ? jsonObject.getStr("f3"):"0"));
//	        ret.setNowPrice(new BigDecimal(!Objects.equals(jsonObject.getStr("f2"), "-") ? jsonObject.getStr("f2"):"0"));
//	        ret.setHeightPrice(new BigDecimal(!Objects.equals(jsonObject.getStr("f15"), "-") ? jsonObject.getStr("f15"):"0"));
//	        ret.setLowPrice(new BigDecimal(!Objects.equals(jsonObject.getStr("f16"), "-") ? jsonObject.getStr("f16"):"0"));
//	        ret.setOpenPrice(new BigDecimal(!Objects.equals(jsonObject.getStr("f17"), "-") ? jsonObject.getStr("f17"):"0"));
//	        ret.setVolume(new BigDecimal(!Objects.equals(jsonObject.getStr("f6"), "-") ? jsonObject.getStr("f6"):"0"));
//	        ret.setTurnover(new BigDecimal(!Objects.equals(jsonObject.getStr("f5"), "-") ? jsonObject.getStr("f5"):"0"));
//	        ret.setPrevClose(new BigDecimal(!Objects.equals(jsonObject.getStr("f18"), "-") ? jsonObject.getStr("f18"):"0"));
//    	}
//    	return ret;
//    }
	
}
