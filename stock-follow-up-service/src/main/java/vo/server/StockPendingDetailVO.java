package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockPendingDetailVO {
	@ApiModelProperty(value = "委托ID")
	private Integer id;

	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "委托数量")
	private Integer buyingShares;

	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;

	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;

	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;

	@ApiModelProperty(value = "涨幅")
	private BigDecimal increase;

	@ApiModelProperty(value = "委托价格")
	private BigDecimal buyingPrice;

	@ApiModelProperty(value = "持仓状态，0-待提交，1-已委托，2-已成交（正式持仓），3-已撤销，4-已拒绝")
	private Integer positionStatus;

	@ApiModelProperty(value = "是否为大宗交易")
	private Boolean isBlockTrading;

	@ApiModelProperty(value = "委托时间")
	private Date pendingTime;

	@ApiModelProperty(value = "买入时的交易订单号")
	private String tradingOrderSn;

	@ApiModelProperty(value = "买入手续费")
	private BigDecimal buyingFee;

	@ApiModelProperty(value = "买入时的印花税")
	private BigDecimal buyingStampDuty;
	
	@ApiModelProperty(value = "委托金额")
	private BigDecimal orderAmount;
	
	@ApiModelProperty(value = "实际金额")
	private BigDecimal finalAmount;
}
