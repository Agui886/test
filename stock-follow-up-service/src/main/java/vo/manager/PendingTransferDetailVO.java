package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PendingTransferDetailVO {
	
	@ApiModelProperty(value = "委托ID")
	private Integer id;
	
	@ApiModelProperty(value = "股票名称")
	private String stockName;
	
	@ApiModelProperty(value = "股票类型")
	private String stockType;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;

	@ApiModelProperty(value = "买入股数")
	private Integer buyingShares;
	
	@ApiModelProperty(value = "一手的股数（美股/港股：通过接口获取实时数据，A股：所有股票每手股数固定为100股）")
    private Integer sharesOfHand;
	
}
