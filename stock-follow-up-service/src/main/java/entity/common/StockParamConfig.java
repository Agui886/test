package entity.common;

import java.math.BigDecimal;

import enums.CurrencyEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockParamConfig {
	
	/**跟投设置 **/
	@ApiModelProperty("开始跟投时间节点-限制（例：12:00）")
	private String followStartTimeRestrict = "12:00";
	@ApiModelProperty("跟投仓位设置，逗号分隔（例：0.25,0.5,1）")
	private String followPositionSet = "0.25,0.5,1";
	/**跟投设置 **/
	
	/**股票交易时间设置 **/
	@ApiModelProperty("A股上午开始交易时间（例：09:30）")
	private String marketA_amTradingStart = "09:30";
	@ApiModelProperty("A股上午结束交易时间（例：11:30）")
	private String marketA_amTradingEnd = "11:30";
	@ApiModelProperty("A股下午开始交易时间（例：13:30）")
	private String marketA_pmTradingStart = "13:30";
	@ApiModelProperty("A股下午结束交易时间（例：15:00）")
	private String marketA_pmTradingEnd = "15:00";
	@ApiModelProperty("美股上午开始交易时间（例：09:30）")
	private String marketUs_amTradingStart = "09:30";
	@ApiModelProperty("美股上午结束交易时间（例：12:00）")
	private String marketUs_amTradingEnd = "12:00";
	@ApiModelProperty("美股下午开始交易时间（例：12:00）")
	private String marketUs_pmTradingStart = "12:00";
	@ApiModelProperty("美股下午结束交易时间（例：16:00）")
	private String marketUs_pmTradingEnd = "16:00";
	@ApiModelProperty("港股上午开始交易时间（例：09:30）")
	private String marketHk_amTradingStart = "09:30";
	@ApiModelProperty("港股上午结束交易时间（例：12:00）")
	private String marketHk_amTradingEnd = "12:00";
	@ApiModelProperty("港股下午开始交易时间（例：13:00）")
	private String marketHk_pmTradingStart = "13:00";
	@ApiModelProperty("港股下午结束交易时间（例：16:00）")
	private String marketHk_pmTradingEnd = "16:00";
	/**股票交易时间设置**/
	
	/**大宗交易时间设置 **/
	@ApiModelProperty("大宗-A股上午开始交易时间（例：09:30）")
	private String bulkA_amTradingStart = "09:30";
	@ApiModelProperty("大宗-A股上午结束交易时间（例：11:30）")
	private String bulkA_amTradingEnd = "11:30";
	@ApiModelProperty("大宗-A股下午开始交易时间（例：13:30）")
	private String bulkA_pmTradingStart = "13:30";
	@ApiModelProperty("大宗-A股下午结束交易时间（例：15:00）")
	private String bulkA_pmTradingEnd = "15:00";
	@ApiModelProperty("大宗-美股上午开始交易时间（例：09:30）")
	private String bulkUs_amTradingStart = "09:30";
	@ApiModelProperty("大宗-美股上午结束交易时间（例：12:00）")
	private String bulkUs_amTradingEnd = "12:00";
	@ApiModelProperty("大宗-美股下午开始交易时间（例：12:00）")
	private String bulkUs_pmTradingStart = "12:00";
	@ApiModelProperty("大宗-美股下午结束交易时间（例：16:00）")
	private String bulkUs_pmTradingEnd = "16:00";
	@ApiModelProperty("大宗-港股上午开始交易时间（例：09:30）")
	private String bulkHk_amTradingStart = "09:30";
	@ApiModelProperty("大宗-港股上午结束交易时间（例：12:00）")
	private String bulkHk_amTradingEnd = "12:00";
	@ApiModelProperty("大宗-港股下午开始交易时间（例：13:00）")
	private String bulkHk_pmTradingStart = "13:00";
	@ApiModelProperty("大宗-港股下午结束交易时间（例：16:00）")
	private String bulkHk_pmTradingEnd = "16:00";
	/**大宗交易时间设置**/
	
	
	/**提现时间设置**/
	@ApiModelProperty("开始提现时间（例：09:00）")
	private String startWithdrawalTime = "09:00";
	@ApiModelProperty("结束提现时间（例：17:00）")
	private String endWithdrawalTime = "17:00";
	/**提现时间设置**/
	
	/** 汇率设置 **/
	@ApiModelProperty("是否启用汇率配置")
	private boolean enableExchangeRateConfig;
	@ApiModelProperty("人民币->港元汇率")
	private BigDecimal cny2hkd = CurrencyEnum.HKD.getDefaultExchangeRate();
	@ApiModelProperty("人民币->美元汇率")
	private BigDecimal cny2usd = CurrencyEnum.USD.getDefaultExchangeRate();
	/** 汇率设置 **/
	/** 费用设置 **/
	@ApiModelProperty("A股买入手续费比例（例:0.001）")
	private BigDecimal marketABuyingFeeRate = BigDecimal.ZERO;
	@ApiModelProperty("A股卖出手续费比例（例:0.001）")
	private BigDecimal marketASellingFeeRate = BigDecimal.ZERO;
	@ApiModelProperty("A股印花税比例（例:0.001），卖收取")
	private BigDecimal marketAStampDutyRate = BigDecimal.ZERO;
	@ApiModelProperty("港股买入手续费比例（例:0.001）")
	private BigDecimal marketHkBuyingFeeRate = BigDecimal.ZERO;
	@ApiModelProperty("港股卖出手续费比例（例:0.001）")
	private BigDecimal marketHkSellingFeeRate = BigDecimal.ZERO;
	@ApiModelProperty("港股印花税比例（例:0.001），买和卖收取")
	private BigDecimal marketHkStampDutyRate = BigDecimal.ZERO;
	@ApiModelProperty("美股买入手续费比例（例:0.001）")
	private BigDecimal marketUsBuyingFeeRate = BigDecimal.ZERO;
	@ApiModelProperty("美股卖出手续费比例（例:0.001）")
	private BigDecimal marketUsSellingFeeRate = BigDecimal.ZERO;
	/** 费用设置 **/
	/**购买设置**/
	@ApiModelProperty("最小购买金额（例:1000），设置0为不限制")
	private BigDecimal minBuyingAmount = BigDecimal.ZERO;
	@ApiModelProperty("最大购买金额（例:1000），设置0为不限制")
	private BigDecimal maxBuyingAmount = BigDecimal.ZERO;
	@ApiModelProperty("最小购买股数（例:5000），设置0为不限制")
	private int minBuyingShares = 0;
	@ApiModelProperty("最大购买股数（例:1000000），设置0为不限制")
	private int maxBuyingShares = 0;
	@ApiModelProperty("杠杆倍数（例:1/5/10/20）")
	private String levers = "1/5/10/20";
	@ApiModelProperty("融资年化利率")
	private BigDecimal annualizedInterestRate = new BigDecimal(0.08);
	@ApiModelProperty("买入多长时间内不能平仓/分钟（例:30）")
	private int closingMinutesLimitAfterBuying = 30;
	@ApiModelProperty("设置多少分钟内同一只股票不得下单多少次(同一用户)-分钟设置，0为不限制")
	private int minutesLimimtOfBuyingTimes;
	@ApiModelProperty("设置多少分钟内同一只股票不得下单多少次(同一用户)-次数设置，0为不限制")
	private int timesLimimtOfBuyingTimes;
	@ApiModelProperty("设置多少分钟内交易股数不得超过多少股(同一用户)-分钟设置，0为不限制")
	private int minutesLimimtOfBuyingShares;
	@ApiModelProperty("设置多少分钟内交易股数不得超过多少股(同一用户)-股数设置，0为不限制")
	private int sharesLimimtOfBuyingShares;
	@ApiModelProperty("A股锁仓T+N天数")
	private int marketALockInPeriod = 1;
	@ApiModelProperty("美股锁仓T+N天数")
	private int marketUsLockInPeriod;
	@ApiModelProperty("港股锁仓T+N天数")
	private int marketHkLockInPeriod;
	/**购买设置**/
	/**充提设置**/
	@ApiModelProperty("最小充值金额（例：1000），0为不限制")
	private BigDecimal minCashinAmount = BigDecimal.ZERO;
	@ApiModelProperty("最小提现金额（例：1000），0为不限制")
	private BigDecimal minCashoutAmount = BigDecimal.ZERO;
	@ApiModelProperty("提现手续费（例：5）")
	private BigDecimal cashoutFee = BigDecimal.ZERO;
	@ApiModelProperty("提现手续费比例（例：0.005）")
	private BigDecimal cashoutFeeRate = BigDecimal.ZERO;
	/**充提设置**/
	
	/**用户设置**/
	@ApiModelProperty("密码错误的最大次数")
	private int maxTimesOfIncoreectPassword = 5;
	@ApiModelProperty("融资账户开通金额（例:50000）")
	private BigDecimal financingAccountOpeningAmount = BigDecimal.ZERO;
	/**用户设置**/
	
	/**新股设置**/
	@ApiModelProperty("0元申购-名称")
	private String zeroSubscriptionName = "0元申购";
	@ApiModelProperty("0元申购-次数，0为不限制")
	private int zeroSubscriptionCount = 1;
	@ApiModelProperty("现金申购-名称")
	private String cashSubscriptionName = "现金申购";
	@ApiModelProperty("现金申购-次数，0为不限制")
	private int cashSubscriptionCount = 1;
	@ApiModelProperty("融资申购-名称")
	private String financingSubscriptionName = "融资申购";
	@ApiModelProperty("融资申购-次数，0为不限制")
	private int financingSubscriptionCount = 1;
	/**新股设置**/
	
	/** 开关配置 **/
	@ApiModelProperty("是否启用-融资账户-配置")
	private boolean enableFundingStatusConfig;
	@ApiModelProperty("是否启用-A股市场-配置")
	private boolean enableAMarketConfig;
	@ApiModelProperty("是否启用-港股市场-配置")
	private boolean enableHkMarketConfig;
	@ApiModelProperty("是否启用-美股市场-配置")
	private boolean enableUsMarketConfig;
	/** 开关配置 **/
	
}
