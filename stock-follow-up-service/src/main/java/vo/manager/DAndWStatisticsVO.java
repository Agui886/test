package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DAndWStatisticsVO {
	
	@ApiModelProperty("今日充值")
	private BigDecimal todayCashinAmount;
	
	@ApiModelProperty("总充值")
	private BigDecimal totalCashinAmount;
	
	@ApiModelProperty("今日提现")
	private BigDecimal todayCashoutAmount;
	
	@ApiModelProperty("总提现")
	private BigDecimal totalCashoutAmount;
}
