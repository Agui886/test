package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserAmtDetailVO {
	@ApiModelProperty("用户ID")
	private Integer userId;
	@ApiModelProperty("代理ID")
	private Integer agentId;
	@ApiModelProperty("代理名称ID")
	private String agentName;
	@ApiModelProperty("账户类型")
	private Integer accountType;
	@ApiModelProperty("总资金")
	private BigDecimal totalAmt;
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
	@ApiModelProperty(value = "人民币兑换美元汇率")
	private BigDecimal usdExchangeRate;
	@ApiModelProperty(value = "人民币兑换港元汇率")
	private BigDecimal hkdExchangeRate;
}
