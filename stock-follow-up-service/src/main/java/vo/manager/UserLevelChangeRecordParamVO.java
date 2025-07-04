package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserLevelChangeRecordParamVO extends BasePage {

	@ApiModelProperty("升降级Id")
	private Integer id;
	
	@ApiModelProperty("用户Id")
	private Integer userId;
	
	@ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
    private Integer accountType;
	
	@ApiModelProperty(value = "会员等级id")
    private Integer levelId;
	
	@ApiModelProperty(value = "上级的ID，0表示没有上级")
    private Integer agentId;

    @ApiModelProperty(value = "总代理")
    private Integer generalAgentId;
    
    @ApiModelProperty(value = "变更类型，-1-降级，1-升级")
    private Integer changeType;
	
    @ApiModelProperty("变更时间-开始")
   	private Date createTimeStart;
    
    @ApiModelProperty("变更时间-结束")
   	private Date createTimeEnd;
}
