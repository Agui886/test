package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStockClosingPositionDetailVO {

	@ApiModelProperty(value = "平仓ID")
	private Integer id;

	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;

	@ApiModelProperty(value = "涨幅")
	private BigDecimal increase;

	@ApiModelProperty(value = "持仓价格")
	private BigDecimal buyingPrice;

	@ApiModelProperty(value = "实际盈亏")
	private BigDecimal actualProfit;

	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;

	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;

	@ApiModelProperty(value = "成交价格")
	private BigDecimal sellingPrice;

	@ApiModelProperty(value = "卖出股数")
	private Integer sellingShares;

	@ApiModelProperty(value = "卖出手续费")
	private BigDecimal sellingFee;

	@ApiModelProperty(value = "卖出印花税")
	private BigDecimal sellingStampDuty;

	@ApiModelProperty(value = "点差费")
	private BigDecimal spreadFee;

	@ApiModelProperty(value = "卖出时的交易订单号")
	private String tradingOrderSn;

	@ApiModelProperty(value = "平仓、下单时间")
	private Date closingTime;
	
	@ApiModelProperty(value = "成交时间")
	private Date transactionTime;
}
