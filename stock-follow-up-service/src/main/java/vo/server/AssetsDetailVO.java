package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AssetsDetailVO {

	@ApiModelProperty("总资产")
	private BigDecimal totalAssets;

	@ApiModelProperty(value = "可用金额")
	private BigDecimal availableAmt;

	@ApiModelProperty(value = "交易中的冻结金额")
	private BigDecimal tradingFrozenAmt;

	@ApiModelProperty(value = "跟投资金")
    private BigDecimal followAmt;

	@ApiModelProperty(value = "可提现金额")
	private BigDecimal availableWithdrawalAmt;

	@ApiModelProperty(value = "滞留金")
	private BigDecimal detentionAmt;
	
	@ApiModelProperty(value = "持仓总市值")
	private BigDecimal totalMarketValue;

	@ApiModelProperty(value = "持仓总盈亏")
	private BigDecimal totalProfit;
	
	@ApiModelProperty(value = "今日盈亏")
	private BigDecimal todayProfit;
	
	@ApiModelProperty(value = "今日盈亏比例")
	private BigDecimal todayProfitRate;
	
	@ApiModelProperty(value = "持仓总数")
	private int userStockPositionTotal;
	
	@ApiModelProperty(value = "挂单总数")
	private int userStockPendingTotal;
	
	@ApiModelProperty(value = "平仓总数")
	private int userStockClosingPositionTotal;
	
	@ApiModelProperty(value = "累计总盈亏")
	private BigDecimal aggregateTotalProfit;
	
	
}
