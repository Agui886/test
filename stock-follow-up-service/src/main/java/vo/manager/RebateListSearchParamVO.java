package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateListSearchParamVO extends BasePage {

	@ApiModelProperty("用户Id")
	private Integer userId;
	
	@ApiModelProperty(value = "会员等级id")
    private Integer levelId;
	
	@ApiModelProperty(value = "上级的ID，0表示没有上级")
    private Integer agentId;

    @ApiModelProperty(value = "总代理")
    private Integer generalAgentId;
    
    @ApiModelProperty("变更时间-开始")
   	private Date changeTimeStart;
    
    @ApiModelProperty("变更时间-结束")
   	private Date changeTimeEnd;
}
