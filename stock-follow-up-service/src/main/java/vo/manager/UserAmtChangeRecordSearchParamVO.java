package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserAmtChangeRecordSearchParamVO extends BasePage {

	@ApiModelProperty("用户ID")
	private Integer userId;

	@ApiModelProperty(value = "代理ID")
	private Integer agentId;

	@ApiModelProperty(value = "变更类型, null-查全部")
	private String deType;
	
	@ApiModelProperty(value = "开始时间")
	private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
	private Date endTime;
}
