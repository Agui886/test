package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateDetailParamVO extends BasePage {
	
	@ApiModelProperty("会员id")
	private Integer userId;
	
	@ApiModelProperty("贡献会员id")
	private Integer fromUserId;
	
	@ApiModelProperty("贡献会员等级，传null或0查全部")
	private Integer fromUserLevelVal;
	
	@ApiModelProperty("贡献直属代理id，传null或0查全部")
	private Integer fromUserAgentId;

	@ApiModelProperty("贡献总代理id， 传null或0查全部")
	private Integer fromUserGeneralAgentId;
	
	@ApiModelProperty("查询时间开始")
	private Date startDate;
	
	@ApiModelProperty("查询时间结束")
	private Date endDate;
}
