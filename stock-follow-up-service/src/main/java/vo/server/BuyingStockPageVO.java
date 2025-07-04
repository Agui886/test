package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuyingStockPageVO {
	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

//	@ApiModelProperty(value = "是否可交易")
//	private Boolean isTradable;

	@ApiModelProperty(value = "涨幅")
	private BigDecimal increase;

	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;

	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "杠杆倍数")
	private String[] levers;

	@ApiModelProperty(value = "手续费率，手续费=每股价格*股数*手续费率")
	private BigDecimal buyingFeeRate;

	@ApiModelProperty(value = "印花税费率，印花税=每股价格*股数*印花税费率")
	private BigDecimal stampDutyRate;

//	@ApiModelProperty(value = "现金最多可买股数")
//	private BigDecimal sharesWithCash;
//	
//	@ApiModelProperty(value = "融资最多可买股数")
//	private BigDecimal sharesWithFinancing;

	@ApiModelProperty(value = "是否已开通融资")
	private boolean financingAvailable;

	@ApiModelProperty(value = "人民币兑换该股票币种汇率")
	private BigDecimal exchangeRate = BigDecimal.ONE;

	@ApiModelProperty(value = "融资年化利率")
	private BigDecimal annualizedInterestRate;
	
	@ApiModelProperty(value = "一手的股数（美股/港股：通过接口获取实时数据，A股：所有股票每手股数固定为100股）")
    private Integer sharesOfHand;
	
	@ApiModelProperty(value = "市场状态，0-已收盘，1-午间休市，2-交易中")
	private Integer marketStatus;

}
