package vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockDayTransactionDataPageVO {

	@ApiModelProperty(value = "下标")
	private Integer sub;
	
	@ApiModelProperty(value = "价格")
	private String price;
	
	@ApiModelProperty(value = "交易时间")
	private String tradeTime;
	
	@ApiModelProperty(value = "交易日期")
	private String tradeDay;
	
	@ApiModelProperty(value = "交易量")
	private String volume;
	
}
