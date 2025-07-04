package vo.server;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockPlateQuotesVO {
	
	@ApiModelProperty("板块名称")
	private String plateName;
	
	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;
	
	@ApiModelProperty(value = "股票名称")
	private String firstStockName;
	
	@ApiModelProperty(value = "股票的涨幅比例")
	private BigDecimal firstStockPercentageIncrease;
	
	@ApiModelProperty(value = "指数趋势")
    private double[] trends;
}
