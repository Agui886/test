package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockPositionListVO {
	@ApiModelProperty(value = "持仓ID")
	private Integer id;
	@ApiModelProperty(value = "股票名称")
	private String stockName;
	@ApiModelProperty(value = "股票代码")
	private String stockCode;
	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;
	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;
	@ApiModelProperty(value = "持仓类型，实盘-0，模拟-1")
	private Integer positionType;
	@ApiModelProperty(value = "用户ID")
	private Integer userId;
	@ApiModelProperty(value = "代理ID")
	private Integer agentId;
	@ApiModelProperty(value = "代理名称")
	private String agentName;
	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;
	@ApiModelProperty(value = "锁仓天数")
	private Integer lockInPeriod;
	@ApiModelProperty(value = "买入价格")
	private BigDecimal buyingPrice;
	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;
	@ApiModelProperty(value = "浮动盈亏")
	private BigDecimal floatingProfit;
	@ApiModelProperty(value = "买入(当前持有)股数")
	private Integer buyingShares;
	@ApiModelProperty(value = "总市值")
	private BigDecimal marketValue;
	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;
	@ApiModelProperty(value = "是否锁仓")
	private Boolean isLock;
	@ApiModelProperty(value = "锁仓原因描述")
	private String lockMsg;
	@ApiModelProperty(value = "建仓时间")
	private Date positionTime;
	@ApiModelProperty("冻结股数")
	private Integer unavailableShares;
	@ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal exchangeRate = BigDecimal.ONE;
}
