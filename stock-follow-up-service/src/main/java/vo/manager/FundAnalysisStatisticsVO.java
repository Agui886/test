package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FundAnalysisStatisticsVO {
	
	@ApiModelProperty("可用金额-总额")
	private BigDecimal availableAmtTotal;
	
	@ApiModelProperty("交易中的冻结金额-总额")
	private BigDecimal tradingFrozenAmtTotal;
	
	@ApiModelProperty("跟投资金-总额")
	private BigDecimal followAmtTotal;
	
	@ApiModelProperty(value = "持仓总市值（人名币）-总额")
	private BigDecimal marketValueCnyTotal;
}
