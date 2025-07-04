package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateRecordParamVO extends BasePage {
	
	@ApiModelProperty("会员id")
	private Integer userId;
	
	@ApiModelProperty("会员等级，传null或0查全部")
	private Integer userLevelVal;
	
	@ApiModelProperty("直属代理id，传null或0查全部")
	private Integer agentId;

	@ApiModelProperty("总代理id， 传null或0查全部")
	private Integer generalAgentId;
	
	@ApiModelProperty("查询时间开始")
	private Date startDate;
	
	@ApiModelProperty("查询时间结束")
	private Date endDate;
}
