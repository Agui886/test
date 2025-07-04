package service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import config.RedisDbTypeEnum;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.CurrencyEnum;
import enums.StockMarketTypeEnum;
import enums.StockRealTimeApiEnum;
import lombok.extern.slf4j.Slf4j;
import redis.RedisKeyPrefix;
import service.SysParamConfigService;
import utils.BuyAndSellUtils;
import utils.NowStockApi;
import utils.QosApi;
import utils.RedisDao;
import utils.SinaApi;
import utils.StringUtil;
import vo.common.StockQuotesVO;

/**
 * <p>
 * 系统参数配置 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-11-13
 */
@Service
@Slf4j
public class SysParamConfigServiceImpl implements  SysParamConfigService {

	@Resource
	private RedisDao redisDao;
	
	private final String hhmm_pattern = "^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$";
	
	private final String lever_pattern = "^(?!^/)[0-9/]+(?<!/$)$";
	
	private final StockRealTimeApiEnum stockRealTimeApi = StockRealTimeApiEnum.NOW_API;//更改api供应商，调用不同的实现
	
	@Override
	public StockParamConfig getSysParamConfig() {
		String key = RedisKeyPrefix.getSysParamConfigKey();
		StockParamConfig c = redisDao.getBean(key, StockParamConfig.class);
		if(c == null) {
			c = new StockParamConfig();
			redisDao.setBean(key, c);
		}
		return c;
	}

	@Override
	public Response<Void> saveStockParamConfig(StockParamConfig c) {
		if(!Pattern.matches(hhmm_pattern, c.getMarketA_amTradingStart())) {
			return Response.fail("A股上午开始交易时间格式不对，（例：09:15）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketA_amTradingEnd())) {
			return Response.fail("A股上午结束交易时间格式不对，（例：11:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketA_pmTradingStart())) {
			return Response.fail("A股下午开始交易时间格式不对，（例：13:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketA_pmTradingEnd())) {
			return Response.fail("A股下午结束交易时间格式不对，（例：15:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketUs_amTradingStart())) {
			return Response.fail("美股上午开始交易时间格式不对，（例：09:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketUs_amTradingEnd())) {
			return Response.fail("美股上午结束交易时间格式不对，（例：12:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketUs_pmTradingStart())) {
			return Response.fail("美股下午开始交易时间格式不对，（例：12:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketUs_pmTradingEnd())) {
			return Response.fail("美股下午结束交易时间格式不对，（例：16:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketHk_amTradingStart())) {
			return Response.fail("港股上午开始交易时间格式不对，（例：09:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketHk_amTradingEnd())) {
			return Response.fail("港股上午结束交易时间格式不对，（例：12:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketHk_pmTradingStart())) {
			return Response.fail("港股下午开始交易时间格式不对，（例：13:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getMarketHk_pmTradingEnd())) {
			return Response.fail("港股下午结束交易时间格式不对，（例：16:00）");
		}
		
		if(!Pattern.matches(hhmm_pattern, c.getBulkA_amTradingStart())) {
			return Response.fail("A股上午开始交易时间格式不对，（例：09:15）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkA_amTradingEnd())) {
			return Response.fail("A股上午结束交易时间格式不对，（例：11:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkA_pmTradingStart())) {
			return Response.fail("A股下午开始交易时间格式不对，（例：13:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkA_pmTradingEnd())) {
			return Response.fail("A股下午结束交易时间格式不对，（例：15:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkUs_amTradingStart())) {
			return Response.fail("美股上午开始交易时间格式不对，（例：09:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkUs_amTradingEnd())) {
			return Response.fail("美股上午结束交易时间格式不对，（例：12:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkUs_pmTradingStart())) {
			return Response.fail("美股下午开始交易时间格式不对，（例：12:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkUs_pmTradingEnd())) {
			return Response.fail("美股下午结束交易时间格式不对，（例：16:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkHk_amTradingStart())) {
			return Response.fail("港股上午开始交易时间格式不对，（例：09:30）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkHk_amTradingEnd())) {
			return Response.fail("港股上午结束交易时间格式不对，（例：12:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkHk_pmTradingStart())) {
			return Response.fail("港股下午开始交易时间格式不对，（例：13:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getBulkHk_pmTradingEnd())) {
			return Response.fail("港股下午结束交易时间格式不对，（例：16:00）");
		}
		
		if(!Pattern.matches(hhmm_pattern, c.getStartWithdrawalTime())) {
			return Response.fail("开始提现时间格式不对，（例：09:00）");
		}
		if(!Pattern.matches(hhmm_pattern, c.getEndWithdrawalTime())) {
			return Response.fail("结束提现时间格式不对，（例：17:00）");
		}
		
		if(c.getCny2hkd().compareTo(BigDecimal.ZERO) < 0) {
			c.setCny2hkd(CurrencyEnum.HKD.getDefaultExchangeRate());
		}
		if(c.getCny2usd().compareTo(BigDecimal.ZERO) < 0) {
			c.setCny2usd(CurrencyEnum.USD.getDefaultExchangeRate());
		}
		
		if (c.getMarketABuyingFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketABuyingFeeRate(BigDecimal.ZERO);
		}
		if (c.getMarketASellingFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketASellingFeeRate(BigDecimal.ZERO);
		}
		if (c.getMarketAStampDutyRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketAStampDutyRate(BigDecimal.ZERO);
		}
		if (c.getMarketHkBuyingFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketHkBuyingFeeRate(BigDecimal.ZERO);
		}
		if (c.getMarketHkSellingFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketHkSellingFeeRate(BigDecimal.ZERO);
		}
		if (c.getMarketHkStampDutyRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketHkStampDutyRate(BigDecimal.ZERO);
		}
		if (c.getMarketUsBuyingFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketUsBuyingFeeRate(BigDecimal.ZERO);
		}
		if (c.getMarketUsSellingFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setMarketUsSellingFeeRate(BigDecimal.ZERO);
		}
		
		if (c.getMinBuyingAmount().compareTo(BigDecimal.ZERO) < 0) {
			c.setMinBuyingAmount(BigDecimal.ZERO);
		}
		if (c.getMaxBuyingAmount().compareTo(BigDecimal.ZERO) < 0) {
			c.setMaxBuyingAmount(BigDecimal.ZERO);
		}
		if (c.getMinBuyingShares() < 0) {
			c.setMinBuyingShares(0);
		}
		if (c.getMaxBuyingShares() < 0) {
			c.setMaxBuyingShares(0);
		}
		if(!Pattern.matches(lever_pattern, c.getLevers())) {
			return Response.fail("杠杆倍数格式不对，（例:1/5/10/20）");
		}
		if (c.getAnnualizedInterestRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setAnnualizedInterestRate(BigDecimal.ZERO);
		}
		if (c.getClosingMinutesLimitAfterBuying() < 0) {
			c.setClosingMinutesLimitAfterBuying(0);
		}
		if (c.getMinutesLimimtOfBuyingTimes() < 0) {
			c.setMinutesLimimtOfBuyingTimes(0);
		}
		if (c.getTimesLimimtOfBuyingTimes() < 0) {
			c.setTimesLimimtOfBuyingTimes(0);
		}
		if (c.getMinutesLimimtOfBuyingShares() < 0) {
			c.setMinutesLimimtOfBuyingShares(0);
		}
		if (c.getSharesLimimtOfBuyingShares() < 0) {
			c.setSharesLimimtOfBuyingShares(0);
		}
		if (c.getMarketALockInPeriod() < 0) {
			c.setMarketALockInPeriod(0);
		}
		if (c.getMarketUsLockInPeriod() < 0) {
			c.setMarketUsLockInPeriod(0);
		}
		if (c.getMarketHkLockInPeriod() < 0) {
			c.setMarketHkLockInPeriod(0);
		}
		
		if (c.getMinCashinAmount().compareTo(BigDecimal.ZERO) < 0) {
			c.setMinCashinAmount(BigDecimal.ZERO);
		}
		if (c.getMinCashoutAmount().compareTo(BigDecimal.ZERO) < 0) {
			c.setMinCashoutAmount(BigDecimal.ZERO);
		}
		if (c.getCashoutFee().compareTo(BigDecimal.ZERO) < 0) {
			c.setCashoutFee(BigDecimal.ZERO);
		}
		if (c.getCashoutFeeRate().compareTo(BigDecimal.ZERO) < 0) {
			c.setCashoutFeeRate(BigDecimal.ZERO);
		}
		
		if (c.getMaxTimesOfIncoreectPassword() < 0) {
			c.setMaxTimesOfIncoreectPassword(0);
		}
		if (c.getFinancingAccountOpeningAmount().compareTo(BigDecimal.ZERO) < 0) {
			c.setFinancingAccountOpeningAmount(BigDecimal.ZERO);
		}
		
		if (c.getZeroSubscriptionCount() < 0) {
			c.setZeroSubscriptionCount(0);
		}
		if (c.getCashSubscriptionCount() < 0) {
			c.setCashSubscriptionCount(0);
		}
		if (c.getFinancingSubscriptionCount() < 0) {
			c.setFinancingSubscriptionCount(0);
		}
		
		redisDao.setBean(RedisKeyPrefix.getSysParamConfigKey(), c);
		return Response.success();
	}
	
	@Override
	public BigDecimal getExchangeRate(CurrencyEnum currency) {
		if(currency == CurrencyEnum.CNY)
			return BigDecimal.ONE;
		//获取汇率配置
		StockParamConfig c = this.getSysParamConfig();
		if(c.isEnableExchangeRateConfig()) {
			switch (currency) {
			case USD:
				if(c.getCny2usd() == null) {
					log.info("启用了系统配置汇率，但未配置具体值,将返回常量美元汇率");
					return currency.getDefaultExchangeRate();
				}
				return c.getCny2usd();
			default:
				if(c.getCny2hkd() == null) {
					log.info("启用了系统配置汇率，但未配置具体值,将返回常量港汇率");
					return currency.getDefaultExchangeRate();
				}
				return c.getCny2hkd();
			}
		}			
		//使用外网汇率
		BigDecimal	exchangeRate = SinaApi.getExchangeRate(currency);
		if(exchangeRate == null) {
			log.info("查询外网汇率失败，将返回默认汇率");
			exchangeRate = currency.getDefaultExchangeRate();
		}
		log.info("未启用系统配置汇率，将使用外网汇率：{}", exchangeRate);
		return exchangeRate;
	}

	/**
	 * 根据股票市场类型code，获取股票市场交易状态
	 * @throws Exception 
	 */
	@Override
	public int getMarketTradingStatus(int stockMarketTypeCode){
		StockParamConfig stockParamConfig =  getSysParamConfig();
		StockMarketTypeEnum stockMarketTypeEnum = StockMarketTypeEnum.getByCode(stockMarketTypeCode);
		switch (stockMarketTypeEnum) {
		case Market_Hk://港股市场
			return BuyAndSellUtils.getTradingStatus(
					 stockParamConfig.getMarketHk_amTradingStart(), 
					 stockParamConfig.getMarketHk_amTradingEnd(), 
					 stockParamConfig.getMarketHk_pmTradingStart(), 
					 stockParamConfig.getMarketHk_pmTradingEnd(), 
					 "",true);
		case Market_Us://美股市场
			return BuyAndSellUtils.getTradingStatus(
					 stockParamConfig.getMarketUs_amTradingStart(), 
					 stockParamConfig.getMarketUs_amTradingEnd(), 
					 stockParamConfig.getMarketUs_pmTradingStart(), 
					 stockParamConfig.getMarketUs_pmTradingEnd(), 
					 "us",true);
		default://默认A股市场
			return BuyAndSellUtils.getTradingStatus(
					stockParamConfig.getMarketA_amTradingStart(), 
					stockParamConfig.getMarketA_amTradingEnd(), 
					stockParamConfig.getMarketA_pmTradingStart(), 
					stockParamConfig.getMarketA_pmTradingEnd(), 
					"",true);
		}
	}
	
	/**
	 * 获取股票市场请求限制
	 */
	@Override
	public boolean getMarketRequestRestrict(int stockMarketTypeCode, RedisDbTypeEnum redisType, String redisKey) {
		//查rides
		Object object = redisDao.getBean(redisKey, Object.class);
		//如果有缓存
		if (object != null) {
			//美股，盘前交易提前1个小时，盘后交易延后4个半小时，数据任会发生变动，需要单独处理
			if(stockMarketTypeCode == StockMarketTypeEnum.Market_Us.getCode()) {
				StockParamConfig stockParamConfig =  getSysParamConfig();
				int status = BuyAndSellUtils.getTradingStatus(
						delayComputing(stockParamConfig.getMarketUs_amTradingStart(),-1), 
						 stockParamConfig.getMarketUs_amTradingEnd(), 
						 stockParamConfig.getMarketUs_pmTradingStart(), 
						 delayComputing(stockParamConfig.getMarketUs_pmTradingEnd(),+4), 
						 "us",true);
				//已收盘
				if (status == 0) {
					return false;
				}
			}else {
				//如果已收盘
				if (getMarketTradingStatus(stockMarketTypeCode) == 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 延迟计算
	 * 美股，盘前交易提前1个小时，盘后交易延后4个半小时，数据任会发生变动，需要单独处理
	 * @param tradingTime
	 * @param delayTime
	 * @return
	 */
	private static String delayComputing(String tradingTime, int delayTime) {
		String[] tradingTimeArry = tradingTime.split(":");
		Integer tradingTimeInt0 = Integer.valueOf(tradingTimeArry[0])+delayTime ;
		if (tradingTimeInt0 <= 0) {
			tradingTimeInt0 = 0;
		}
		if (tradingTimeInt0<=0) {
			tradingTimeInt0 = 0;
		}
		if (tradingTimeInt0 >= 23) {
			tradingTimeInt0 = 23;
		}
		String tradingTimeStr0 = String.format("%02d", tradingTimeInt0);
		return tradingTimeStr0+":"+tradingTimeArry[1];
	}

	/**
	 * 获取股票实时数据
	 */
	@Override
	public List<StockQuotesVO> getStockRealTimeList(List<String> stockGids) {
		switch (stockRealTimeApi) {
		case QOS_API://qosAPI
			return QosApi.doQosapiToGetFinanceStockRealtime(stockGids);
		default://默认nowAPI
			return NowStockApi.doNowapiToGetFinanceStockRealtime(stockGids);
		}
	}

	/**
	 * 获取股票实时数据单个对象
	 */
	@Override
	public StockQuotesVO getStockRealTimeData(String stockType, String stockCode) {
		StockQuotesVO stockQuotesVO = new StockQuotesVO();
		List<String> stockGids = new ArrayList<String>();
		stockGids.add(stockType + stockCode);
		List<StockQuotesVO> stockQuotesVOList = getStockRealTimeList(stockGids);
		if (stockQuotesVOList != null && stockQuotesVOList.size() > 0) {
			stockQuotesVO = stockQuotesVOList.get(0);
		}
		return stockQuotesVO;
	}
	
	
	public static void main(String[] args) {
		String s = "23:30";
		System.out.println(delayComputing(s,+4));
		
	}
	
}
