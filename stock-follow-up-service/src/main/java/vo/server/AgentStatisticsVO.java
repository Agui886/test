package vo.server;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vo.manager.RebateStatisticsVO.RebateStatistics;

@Data
public class AgentStatisticsVO {

	@ApiModelProperty(value = "返佣统计")
	private RebateStatistics rebateStatistics;
	
	@ApiModelProperty(value = "上月返佣金额")
	private BigDecimal ultRrebateAmount;

	@ApiModelProperty(value = "累计邀请人数")
	private Integer accumulativeRegInvitationNum;

	@ApiModelProperty(value = "团队总人数（不含自己）")
	private Integer memberCount;
	
	@ApiModelProperty(value = "直属会员")
	private Integer underlingUserNum;
	
	@ApiModelProperty(value = "本月新增团队人数")
	private Integer curMemberCount;
	
	@ApiModelProperty(value = "本月新增直属")
	private Integer curUnderlingUserNum;

}
