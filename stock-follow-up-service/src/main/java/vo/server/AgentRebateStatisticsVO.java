package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vo.manager.RebateStatisticsVO.RebateStatistics;

@Data
public class AgentRebateStatisticsVO {

	
	@ApiModelProperty(value = "返佣统计")
	private RebateStatistics rebateStatistics;
	
	@ApiModelProperty(value = "L1返佣统计")
	private RebateStatistics rebateStatisticsLevel1;
	
	@ApiModelProperty(value = "L2返佣统计")
	private RebateStatistics rebateStatisticsLevel2;
	
	@ApiModelProperty(value = "L3返佣统计")
	private RebateStatistics rebateStatisticsLevel3;
	
	@ApiModelProperty(value = "L4返佣统计")
	private RebateStatistics rebateStatisticsLevel4;
	
	@ApiModelProperty(value = "L5返佣统计")
	private RebateStatistics rebateStatisticsLevel5;
	
	
	
	
}
