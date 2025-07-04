package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SellingStockPageVO {
	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;

	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;

	@ApiModelProperty(value = "是否可跟投")
    private Boolean followEnable;

	@ApiModelProperty(value = "涨幅")
	private BigDecimal increase;

	@ApiModelProperty(value = "涨幅比例")
	private BigDecimal percentageIncrease;

	@ApiModelProperty(value = "当前价格")
	private BigDecimal nowPrice;

	@ApiModelProperty(value = "手续费率，手续费=每股价格*股数*手续费率")
	private BigDecimal sellingFeeRate;

	@ApiModelProperty(value = "印花税费率，印花税=每股价格*股数*印花税费率")
	private BigDecimal stampDutyRate;

	@ApiModelProperty(value = "点差费率")
	private BigDecimal spreadRate;
	
	@ApiModelProperty(value = "市场状态，0-已收盘，1-午间休市，2-交易中")
	private Integer marketStatus;
	
	@ApiModelProperty("冻结股数")
	private Integer unavailableShares = 0;
	
	@ApiModelProperty(value = "买入(当前持有)股数")
	private Integer buyingShares;
	
	@ApiModelProperty(value = "用户产生的所有利息")
    private BigDecimal userAllInterestGenerated;
}
