package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TransferPositionSearchParamVO {
	
	@ApiModelProperty("委托ID")
	private Integer id;
	
	@ApiModelProperty("股数")
	private Integer shares;
	
	
}
