package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vo.manager.CashInOutStatisticsVO.CashInOutStatistics;

@Data
public class FollowStatisticsVO {
	
	@ApiModelProperty("跟投统计")
	private FollowStatistics followStatistics;
	
	@ApiModelProperty("每日跟投")
	private FollowStatistics followStatistics0;
	
	@ApiModelProperty("7日跟投")
	private FollowStatistics followStatistics1;
	
	@ApiModelProperty("16日跟投")
	private FollowStatistics followStatistics2;
	
	@ApiModelProperty("38日跟投")
	private FollowStatistics followStatistics3;
	
	@ApiModelProperty("108日跟投")
	private FollowStatistics followStatistics4;
	
	@ApiModelProperty("180日跟投")
	private FollowStatistics followStatistics5;
	
	@ApiModelProperty("360日跟投")
	private FollowStatistics followStatistics6;
	
	@Data
	public static class FollowStatistics {
		
		@ApiModelProperty("去重会员总数")
		private Integer distinctUserCount;
		
		@ApiModelProperty("去重导师总数")
		private Integer distinctTutorCount;
		
		@ApiModelProperty("总记录数")
		private Integer totalRecords;
		
		@ApiModelProperty("初始跟投总额")
		private BigDecimal totalInitialFollowSum;
		
		@ApiModelProperty("退还跟投总额")
		private BigDecimal totalReturnFollowSum;
		
		@ApiModelProperty("导师抽佣总额")
		private BigDecimal totalTutorCommission;
		
		@ApiModelProperty("平台抽佣总额")
		private BigDecimal totalPlatformCommission;
		
		@ApiModelProperty("系统盈亏总额=初始跟投总额-退还跟投总额")
		private BigDecimal totalSysProfitLoss;
		
		public BigDecimal getTotalSysProfitLoss() {
			if (this.totalInitialFollowSum != null && this.totalReturnFollowSum != null) {
				return this.totalInitialFollowSum.subtract(this.totalReturnFollowSum);
			}
			return totalSysProfitLoss;
		}
		
	}
	
}
