package vo.manager;

import entity.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AgentListVO extends UserInfo {
	
	@ApiModelProperty(value = "一级代理名称")
	private String generalParentName;
	
	@ApiModelProperty(value = "直属上级代理名称")
    private String parentName;
	
	@ApiModelProperty(value = "累计邀请人数")
	private Integer inviteesCount;
	
	@ApiModelProperty(value = "团队总人数（不含自己）")
	private Integer memberCount;
	
	@ApiModelProperty(value = "返佣人数")
	private Integer rebateCount;

}
