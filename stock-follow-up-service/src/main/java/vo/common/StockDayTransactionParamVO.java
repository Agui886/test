package vo.common;

import java.math.BigDecimal;
import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockDayTransactionParamVO extends BasePage {

	@ApiModelProperty("股票代码")
	private String stockCode;

	@ApiModelProperty("股票类型")
	private String stockType;

	@ApiModelProperty(value = "显示发布时间")
	private Date displayReleaseTime;

	@ApiModelProperty(value = "购买价格")
	private BigDecimal buyingPrice;

	@ApiModelProperty(value = "是否市场交易价")
	private Boolean marketPrice;

}