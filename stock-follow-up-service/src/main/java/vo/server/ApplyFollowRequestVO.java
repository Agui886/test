package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplyFollowRequestVO {

//	@ApiModelProperty(value = "私域邀请码")
//    private String privateInvitationCode;
	
	@ApiModelProperty("客户端不用传")
	private Integer userId;
	
	@ApiModelProperty(value = "跟投资金")
    private BigDecimal amount;

	@ApiModelProperty(value = "导师id")
	private Integer tutorId;
	
	@ApiModelProperty("项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;
	
	@ApiModelProperty("客户端不用传")
	private String ip;
	
	@ApiModelProperty("客户端不用传")
	private String ipAddress;
	
	@ApiModelProperty("客户端不用传")
	private String operator;
	
}
