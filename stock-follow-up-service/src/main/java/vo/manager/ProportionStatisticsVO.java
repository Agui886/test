package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProportionStatisticsVO {
	
	@ApiModelProperty("股票数量")
	private int stockCount;
	
	@ApiModelProperty("用户总资金")
	private BigDecimal userTotalAmount;
	
	@ApiModelProperty("用户可用资金")
	private BigDecimal userEnableAmount;
	
	@ApiModelProperty("平仓订单")
	private int closingPositionCount;
	
	@ApiModelProperty("持仓订单")
	private int positionCount;
	
	@ApiModelProperty("总入金")
	private BigDecimal totalCashinAmount;
}
