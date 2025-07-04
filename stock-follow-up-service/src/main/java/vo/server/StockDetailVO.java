package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockDetailVO {

	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

//	@ApiModelProperty(value = "是否锁定，锁定后无法交易")
//	private Boolean isLock;

	@ApiModelProperty(value = "涨幅")
	private BigDecimal increase;
	
	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;

	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "当天最高价格")
	private BigDecimal hightPrice;

	@ApiModelProperty(value = "当天最低价格")
	private BigDecimal lowPrice;

	@ApiModelProperty(value = "开盘价格")
	private BigDecimal openPrice;

	@ApiModelProperty(value = "成交量")
	private BigDecimal volume;

	@ApiModelProperty(value = "成交额")
	private BigDecimal turnover;

	@ApiModelProperty(value = "昨日收盘价格")
	private BigDecimal prevClose;
	
	@ApiModelProperty(value = "是否自选")
	private Boolean isFavorite = false;
	
	@ApiModelProperty(value = "市场状态，0-已收盘，1-午间休市，2-交易中")
	private Integer marketStatus;
	
	@ApiModelProperty(value = "一手的股数（美股/港股：通过接口获取实时数据，A股：所有股票每手股数固定为100股）")
    private Integer sharesOfHand;
}
