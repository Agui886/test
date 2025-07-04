package vo.manager;

import java.math.BigDecimal;

import entity.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowStatisticsVO extends UserInfo{
	
	@ApiModelProperty(value = "当前非结束状态的跟投记录数")
    private Integer activeFollowCount;
    
    @ApiModelProperty(value = "当前非结束状态的初始跟投资金总额")
    private BigDecimal activeInitialSum;
    
    @ApiModelProperty(value = "当前非结束状态的当前跟投资金总额")
    private BigDecimal activeCurrentSum;
    
    @ApiModelProperty(value = "当前非结束状态的去重导师数量")
    private Integer activeTutorCount;
    
    @ApiModelProperty(value = "历史所有状态的跟投总记录数")
    private Integer totalFollowCount;
    
    @ApiModelProperty(value = "历史所有状态的初始跟投资金总额")
    private BigDecimal totalInitialSum;
    
    @ApiModelProperty(value = "历史所有状态的退还跟投资金总额")
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
