package vo.manager;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CashInOutStatisticsVO {
	
	@ApiModelProperty("申请-充值统计")
	private CashInOutStatistics applyCashInStatistics;
	
	@ApiModelProperty("通过-充值统计")
	private CashInOutStatistics passCashInStatistics;
	
	@ApiModelProperty("申请-提现统计")
	private CashInOutStatistics applyCashOutStatistics;
	
	@ApiModelProperty("通过-提现统计")
	private CashInOutStatistics passCashOutStatistics;
	
	@Data
	public static class CashInOutStatistics {
		
		@ApiModelProperty("订单金额总额")
		private BigDecimal totalOrderAmount;
		
		@ApiModelProperty("到账金额总额")
		private BigDecimal totalFinalAmount;
		
		@ApiModelProperty("手续费总额")
		private BigDecimal totalFee;
		
		@ApiModelProperty("去重会员总数")
		private Integer distinctUserCount;
		
		@ApiModelProperty("总记录数")
		private Integer totalRecords;
		
	}
}
