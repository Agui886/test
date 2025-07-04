package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateListVO{
	
	@ApiModelProperty("代理id")
	private Integer id;
	
	@ApiModelProperty("昵称")
    private String nickname;
	
	@ApiModelProperty(value = "上级的ID，0表示没有上级")
    private Integer agentId;

	@ApiModelProperty("上级代理名称")
    private String agentName;
	
	@ApiModelProperty(value = "总代理")
    private Integer generalAgentId;
    
    @ApiModelProperty("总代理名称")
    private String generalAgentName;

	@ApiModelProperty(value = "会员等级id")
	private Integer levelId;
	
	@ApiModelProperty("团队总人数")
	private Integer teamCount;
	
	@ApiModelProperty("L1-统计信息")
	private String L1Stats;
	
	@ApiModelProperty("L2-统计信息")
	private String L2Stats;
	
	@ApiModelProperty("L3-统计信息")
	private String L3Stats;
	
	@ApiModelProperty("L4-统计信息")
	private String L4Stats;
	
	@ApiModelProperty("总返佣金额")
	private BigDecimal rebateSum;
	
}
