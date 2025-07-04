package vo.manager;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateStatisticsVO {
	
	@ApiModelProperty(value = "返佣用户List统计")
	private List<RebateStatistics> rebateStatisticsList;
	
	@ApiModelProperty(value = "返佣统计")
	private RebateStatistics rebateStatistics;
	
	@Data
	public static class RebateStatistics {
		
		@ApiModelProperty("用户id")
		private Integer userId;
		
		@ApiModelProperty(value = "用户昵称")
	    private String nickname;
		
		@ApiModelProperty("获取得返佣总金额")
		private BigDecimal totalRebateAmount;
		
		@ApiModelProperty("获得返佣总数")
		private int totalUserId;
		
		@ApiModelProperty(value = "贡献用户总数")
		private Integer totalFromUserId;
		
		@ApiModelProperty(value = "贡献用户当时总业绩")
		private BigDecimal totalPerformance;
		
	}
	
}
