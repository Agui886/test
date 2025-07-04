package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockClosingPositionListVO {

	@ApiModelProperty(value = "平仓ID")
	private Integer id;
	
	@ApiModelProperty(value = "持仓类型，实盘-0，模拟-1")
	private Integer positionType;
	
	@ApiModelProperty(value = "持仓ID")
	private Integer positionId;

	@ApiModelProperty(value = "用户ID")
	private Integer userId;

	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "代理ID")
	private Integer agentId;

	@ApiModelProperty(value = "代理名称")
	private String agentName;

	@ApiModelProperty(value = "交易订单号")
	private String tradingOrderSn;

	@ApiModelProperty(value = "持仓时间")
	private Date positionTime;

	@ApiModelProperty(value = "买入价格")
	private BigDecimal buyingPrice;

	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "浮动盈亏")
	private BigDecimal floatingProfit;

	@ApiModelProperty(value = "实际盈亏")
	private BigDecimal actualProfit;

	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;

	@ApiModelProperty(value = "买入(原先持有)股数")
	private Integer buyingShares;

	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;

	@ApiModelProperty(value = "买入手续费")
	private BigDecimal buyingFee;

	@ApiModelProperty(value = "买入印花税")
	private BigDecimal buyingStampDuty;

	@ApiModelProperty(value = "卖出价格")
	private BigDecimal sellingPrice;

	@ApiModelProperty(value = "卖出股数")
	private BigDecimal sellingShares;

	@ApiModelProperty(value = "卖出手续费")
	private BigDecimal sellingFee;

	@ApiModelProperty(value = "卖出印花税")
	private BigDecimal sellingStampDuty;

	@ApiModelProperty(value = "点差费")
	private BigDecimal spreadFee;

	@ApiModelProperty(value = "人民币兑换该流水币种的汇率")
	private BigDecimal exchangeRate;

	@ApiModelProperty(value = "平仓时间")
	private Date closingTime;
	
	@ApiModelProperty(value = "市值")
	private BigDecimal marketValue;
}
