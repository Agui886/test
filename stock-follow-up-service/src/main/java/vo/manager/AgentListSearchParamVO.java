package vo.manager;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AgentListSearchParamVO extends BasePage {

	@ApiModelProperty("用户Id")
	private Integer userId;
	
	@ApiModelProperty(value = "账户类型，null-查全部, 实盘-0，模拟-1")
    private Integer accountType;
	
	@ApiModelProperty(value = "会员等级id")
    private Integer levelId;
	
	@ApiModelProperty(value = "上级的ID，0表示没有上级")
    private Integer agentId;

    @ApiModelProperty(value = "总代理")
    private Integer generalAgentId;
    
    @ApiModelProperty(value = "null-查全部，是否允许邀请")
    private Boolean promotionEnable;
}
