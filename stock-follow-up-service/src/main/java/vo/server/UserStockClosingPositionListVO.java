package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStockClosingPositionListVO {
	
	@ApiModelProperty(value = "平仓ID")
	private Integer id;

	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "持仓价格")
	private BigDecimal buyingPrice;
	
	@ApiModelProperty(value = "实际盈亏")
	private BigDecimal actualProfit;
	
	@ApiModelProperty(value = "买入方向，0-买涨，1-买跌")
	private Integer positionDirection;

	@ApiModelProperty(value = "杠杆倍数")
	private Integer lever;
	
	@ApiModelProperty(value = "成交价格")
	private BigDecimal sellingPrice;
	
	@ApiModelProperty(value = "卖出股数")
	private BigDecimal sellingShares;
	
	@ApiModelProperty(value = "是否为大宗交易")
	private Boolean isBlockTrading;
	
	
}
