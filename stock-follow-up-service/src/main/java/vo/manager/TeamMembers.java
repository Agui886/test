package vo.manager;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TeamMembers {
	
	@ApiModelProperty("用户id")
	private Integer userId;
	
	@ApiModelProperty("昵称")
	private String nickname;
	
	@ApiModelProperty("等级值")
	private Integer levelVal; 
	
	@ApiModelProperty("等级名称")
	private String levelName;
	
	@ApiModelProperty("直属上级id")
	private Integer memberAgentId;
	
	@ApiModelProperty("直属上级昵称")
	private String memberAgentNickname;
	
	@ApiModelProperty("成员用户id")
	private Integer memberUserId;
	
	@ApiModelProperty("成员昵称")
	private String memberNickname;
	
	@ApiModelProperty("成员等级")
	private Integer memberLevelVal;
	
	@ApiModelProperty("成员等级名称")
	private String memberLevelName;
	
	@ApiModelProperty("成员注册时间")
    private Date memberRegTime;
}
