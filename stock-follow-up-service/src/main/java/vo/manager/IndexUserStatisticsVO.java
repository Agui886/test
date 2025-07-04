package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IndexUserStatisticsVO {
	
	@ApiModelProperty("用户总数")
	private int totalUsers;
	
	@ApiModelProperty("实盘用户总数")
	private int totalRealUsers;
	
	@ApiModelProperty("模拟用户总数")
	private int totalVirtualUsers;
	
	@ApiModelProperty("今日新增实盘用户总数")
	private int todayNewRealUsers;
	
	@ApiModelProperty("今日新增模拟用户总数")
	private int todayNewVirtualUsers;
	
	@ApiModelProperty("等级1用户总数")
	private int totalOfL1;
	
	@ApiModelProperty("等级2用户总数")
	private int totalOfL2;
	
	@ApiModelProperty("等级3用户总数")
	private int totalOfL3;
	
	@ApiModelProperty("等级4用户总数")
	private int totalOfL4;
	
	@ApiModelProperty("等级5用户总数")
	private int totalOfL5;
}
