package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MarketVO {
	
	@ApiModelProperty("大盘名称")
	private String name;
	
	@ApiModelProperty("当前价格")
	private String nowPrice;
   
	@ApiModelProperty("涨跌幅")
	private String increaseRate;
	
	@ApiModelProperty("涨跌额")
	private String increase;

	@ApiModelProperty("类型")
	private String type;
}
