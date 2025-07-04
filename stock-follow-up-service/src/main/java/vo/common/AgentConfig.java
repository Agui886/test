package vo.common;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AgentConfig {
	
	@ApiModelProperty("返佣结算周期，0-实时，1-每日，2-每周，3-每月")
	private Integer rebateSettlementCycle ;
	
	@ApiModelProperty("升降级结算周期，1-每日，2-每周，3-每月")
	private Integer levelSettlementCycle ;
	
	@ApiModelProperty("L2返佣、代理升级/维持条件")
	private Level2 level2;
	
	@ApiModelProperty("L3返佣、代理升级/维持条件")
	private Level3 level3;
	
	@ApiModelProperty("L4返佣、代理升级/维持条件")
	private Level4 level4;
	
	@ApiModelProperty("L5返佣、代理升级/维持条件")
	private Level5 level5;
	
	@Data
	public static class Level2 {
		
		@ApiModelProperty("从L1获得返佣比例%")
		protected BigDecimal rebateFromL1;
		
		@ApiModelProperty("推广普通会员个数")
		protected Integer memberCountOfL1;
		
		@ApiModelProperty("个人当月业绩>=")
		protected BigDecimal persMonthlyPerformance;
		
		@ApiModelProperty("团队当月业绩>=")
		protected BigDecimal teamMonthlyPerformance;
		
	}
	
	@Data
	public static class Level3 extends Level2 {
		
		@ApiModelProperty("从L2获得返佣比例%")
		protected BigDecimal rebateFromL2;
		
		@ApiModelProperty("推广L2会员个数")
		private Integer memberCountOfL2;
		
		@ApiModelProperty("L2代理级别个数")
		protected Integer agentCountOfL2; 
		
	}
	
	@Data
	public static class Level4 extends Level3 {
		
		@ApiModelProperty("从L3获得返佣比例%")
		protected BigDecimal rebateFromL3;
		
		@ApiModelProperty("推广L3会员个数")
		private Integer memberCountOfL3;
		
		@ApiModelProperty("L3代理级别个数")
		protected Integer agentCountOfL3; 
		
		@ApiModelProperty("团队总人数")
		protected Integer teamMemberCount;
	}
	
	@Data
	public static class Level5 extends Level4 {
		
		@ApiModelProperty("从L4获得返佣比例%")
		protected BigDecimal rebateFromL4;
		
		@ApiModelProperty("推广L4会员个数")
		private Integer memberCountOfL4;
		
		@ApiModelProperty("L4代理级别个数")
		protected Integer agentCountOfL4; 
		
	}
	
	public static BigDecimal getRebate(AgentConfig config, int higherLevelVal, int lowerLevelVal) {
		switch(higherLevelVal) {
		case 5:
			switch(lowerLevelVal) {
			case 4:
				return config.getLevel5().getRebateFromL4();
			case 3:
				return config.getLevel5().getRebateFromL3();
			case 2:
				return config.getLevel5().getRebateFromL2();
			default:
				return config.getLevel5().getRebateFromL1();
			}
		case 4:
			switch(lowerLevelVal) {
			case 3:
				return config.getLevel4().getRebateFromL3();
			case 2:
				return config.getLevel4().getRebateFromL2();
			default:
				return config.getLevel4().getRebateFromL1();
			}
		case 3:
			switch(lowerLevelVal) {
			case 2:
				return config.getLevel3().getRebateFromL2();
			default:
				return config.getLevel3().getRebateFromL1();
			}
		default:
			return config.getLevel2().getRebateFromL1();
		}
	}
}
