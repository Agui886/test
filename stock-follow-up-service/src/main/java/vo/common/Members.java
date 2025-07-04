package vo.common;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Members {
	
	@ApiModelProperty("用户id")
	private Integer userId;
	
	@ApiModelProperty("昵称")
	private String nickname;
	
	@ApiModelProperty("等级值")
	private Integer levelVal; 
	
	@ApiModelProperty("等级名称")
	private String levelName;
	
	@ApiModelProperty("直属上级id")
	private Integer agentId;
	
	@ApiModelProperty("下级成员")
	private List<Members> memberList = new ArrayList<>();
}
