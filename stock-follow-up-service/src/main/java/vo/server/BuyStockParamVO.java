package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuyStockParamVO {

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;

	@ApiModelProperty(value = "买入股数")
	private Integer buyingShares;

	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;
	
	@ApiModelProperty(value = "当前价格")
    private BigDecimal nowPrice;
}
