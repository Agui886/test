package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowTransferOperationParamVO {
	
	@ApiModelProperty("委托ID")
	private Integer id;
	
	@ApiModelProperty("转入类型(0:买入，1：卖出)")
	private Integer transferType;
	
	@ApiModelProperty(value = "委托仓位,[0:全仓,1:1/2仓,2:1/3仓,3:1/4仓]")
	private Integer pendingPosition;
	
	@ApiModelProperty(value = "委托价格")
	private BigDecimal pendingPrice;
	
}
