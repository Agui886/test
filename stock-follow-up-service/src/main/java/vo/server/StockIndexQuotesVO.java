package vo.server;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockIndexQuotesVO {
	
	@ApiModelProperty(value = "指数名称")
	private String indexName;
	
	@ApiModelProperty(value = "指数代码")
	private String indexCode;
	
	@ApiModelProperty(value = "涨幅")
	private BigDecimal increase;
	
	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;
	
	@ApiModelProperty(value = "当前价格、点位")
	private BigDecimal nowPrice;
	
	@ApiModelProperty(value = "指数趋势")
    private double[] trends;
}
