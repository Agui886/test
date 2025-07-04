package service;

import java.math.BigDecimal;
import java.util.List;

import config.RedisDbTypeEnum;
import entity.common.Response;
import entity.common.StockParamConfig;
import enums.CurrencyEnum;
import vo.common.StockQuotesVO;

/**
 * <p>
 * 系统参数配置 服务类
 * </p>
 *
 * @author 
 * @since 2024-11-13
 */
public interface SysParamConfigService {
	/**
	 * 获取系统配置
	 * @return
	 */
	StockParamConfig getSysParamConfig();
	/**
	 * 保存系统配置
	 * @param c
	 */
	Response<Void> saveStockParamConfig(StockParamConfig c);
	/**
	 * 获取外币汇率
	 * @param currency
	 * @return
	 */
	BigDecimal getExchangeRate(CurrencyEnum currency);
	/**
	 * 获取股票市场交易状态
	 * @param stockMarketTypeCode 股票市场类型code
	 * @return
	 */
	int getMarketTradingStatus(int stockMarketTypeCode);
	
	/**
	 * 获取股票市场请求限制
	 * @param stockMarketTypeCode 股票市场类型code
	 * @param redisType redis类型
	 * @param redisKey redisKey
	 * @return
	 * @throws Exception
	 */
	boolean getMarketRequestRestrict(int stockMarketTypeCode, RedisDbTypeEnum redisType, String redisKey) ;
	
	/**
	 * 获取股票实时数据List
	 * @param stockGids = stockType+stockCode
	 * @return
	 */
	List<StockQuotesVO> getStockRealTimeList(List<String> stockGids);
	
	/**
	 * 获取股票实时数据单个对象
	 * @param stockType
	 * @param stockCode
	 * @return
	 */
	StockQuotesVO getStockRealTimeData(String stockType, String stockCode);
}
