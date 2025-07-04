package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PendingListVO {
	
	@ApiModelProperty(value = "委托ID")
	private Integer id;
	
	@ApiModelProperty(value = "持仓ID")
	private Integer positionId;
	
	@ApiModelProperty(value = "交易订单号")
	private String tradingOrderSn;

	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "用户ID")
	private Integer userId;
	
	@ApiModelProperty(value = "代理ID")
	private Integer agentId;

	@ApiModelProperty(value = "代理名称")
	private String agentName;

	@ApiModelProperty(value = "买入股数")
	private Integer buyingShares;
	
	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;
	
	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;
	
	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;
	
	@ApiModelProperty(value = "买入价格")
	private BigDecimal buyingPrice;
	
	@ApiModelProperty(value = "买入手续费")
	private BigDecimal buyingFee;
	
	@ApiModelProperty(value = "印花税")
	private BigDecimal buyingStampDuty;

	@ApiModelProperty(value = "持仓状态，0-待提交，1-已委托，2-已成交（正式持仓），3-已撤销，4-已拒绝")
	private Integer positionStatus;

	@ApiModelProperty(value = "是否为大宗交易")
	private Boolean isBlockTrading;
	
	@ApiModelProperty(value = "委托时间")
	private Date pendingTime;
	
	@ApiModelProperty(value = "转入持仓时间")
	private Date positionTime;
	
	@ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal exchangeRate = BigDecimal.ONE;
	
}
