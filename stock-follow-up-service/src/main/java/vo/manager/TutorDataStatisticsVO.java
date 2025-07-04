package vo.manager;

import java.math.BigDecimal;

import entity.TutorInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TutorDataStatisticsVO extends TutorInfo{
	
	@ApiModelProperty(value = "当前非结束状态的去重用户数")
    private Integer currentFollowUsers;
    
    @ApiModelProperty(value = "当前非结束状态的跟投总额")
    private BigDecimal currentFollowSum;
    
    @ApiModelProperty(value = "历史所有状态的去重用户数")
    private Integer totalFollowUsers;
    
    @ApiModelProperty(value = "历史初始跟投总额")
    private BigDecimal totalInitialSum;
    
    @ApiModelProperty(value = "历史退还总额")
    private BigDecimal totalReturnSum;
    
    @ApiModelProperty(value = "导师总抽成")
    private BigDecimal totalTutorCommission;
    
    @ApiModelProperty(value = "平台总抽成")
    private BigDecimal totalPlatformCommission;
    
    @ApiModelProperty("系统盈亏总额=初始跟投总额-退还跟投总额")
	private BigDecimal totalSysProfitLoss;
	
	public BigDecimal getTotalSysProfitLoss() {
		if (this.totalInitialSum != null && this.totalReturnSum != null) {
			return this.totalInitialSum.subtract(this.totalReturnSum);
		}
		return totalSysProfitLoss;
	}
}
