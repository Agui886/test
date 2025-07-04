package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateRecordVO {
	
	@ApiModelProperty("会员id")
	private Integer userId;
	
	@ApiModelProperty("会员昵称")
	private String nickname;
	
	@ApiModelProperty("会员等级")
	private Integer userLevelVal;
	
	@ApiModelProperty("会员等级名称")
	private String userLevelName;

	@ApiModelProperty("直属代理id")
	private Integer agentId;
	
	@ApiModelProperty("直属代理名称")
	private String agentName;

	@ApiModelProperty("总代理id")
	private Integer generalAgentId;

	@ApiModelProperty("总代理名称")
	private String generalAgentName;

	@ApiModelProperty("返佣人数")
	private Integer rebateMemberCount;
	
	@ApiModelProperty("l1-业绩额")
	private BigDecimal lv1Performance;
	
	@ApiModelProperty("l1-返佣金额")
	private BigDecimal lv1RebateAmount;
	
	@ApiModelProperty("l2-业绩额")
	private BigDecimal lv2Performance;
	
	@ApiModelProperty("l2-返佣金额")
	private BigDecimal lv2RebateAmount;
	
	@ApiModelProperty("l3-业绩额")
	private BigDecimal lv3Performance;
	
	@ApiModelProperty("l3-返佣金额")
	private BigDecimal lv3RebateAmount;
	
	@ApiModelProperty("l4-业绩额")
	private BigDecimal lv4Performance;
	
	@ApiModelProperty("l4-返佣金额")
	private BigDecimal lv4RebateAmount;
	
	@ApiModelProperty("总业绩")
	private BigDecimal totalPerformance;
	
	@ApiModelProperty("总返佣金额")
	private BigDecimal totalRebateAmount;
	
	@ApiModelProperty("结算日期")
	private Date settlementDate;
}
