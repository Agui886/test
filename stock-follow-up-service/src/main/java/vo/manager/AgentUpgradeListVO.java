package vo.manager;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AgentUpgradeListVO{
	
	@ApiModelProperty("用户Id")
	private Integer userId;
	
	@ApiModelProperty(value = "昵称")
    private String nickname;
	
	@ApiModelProperty(value = "真实姓名")
    private String realName;
	
	@ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
    private Integer accountType;
	
	@ApiModelProperty(value = "会员等级id")
    private Integer levelId;
	
	@ApiModelProperty(value = "上级的ID，0表示没有上级")
    private Integer agentId;

    @ApiModelProperty(value = "总代理")
    private Integer generalAgentId;
    
    @ApiModelProperty(value = "升级类型(0:维持，1:升级，2:降级)")
    private Integer UpgradeType;
    
    @ApiModelProperty(value = "变更前-会员等级id")
    private Integer beforeLevelId;
    
    @ApiModelProperty(value = "变更后-会员等级id")
    private Integer laterLevelId;
    
    @ApiModelProperty("变更时间")
	private Date changeTime;
	
}
