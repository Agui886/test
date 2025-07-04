package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCountVO {
	
	@ApiModelProperty("代理数量")
	private int agentCount;
	
	@ApiModelProperty("实盘用户")
	private int realUserCount;
	
	@ApiModelProperty("实盘用户")
	private int virtualUserCount;
}
