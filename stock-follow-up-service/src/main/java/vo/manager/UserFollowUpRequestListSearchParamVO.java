package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowUpRequestListSearchParamVO extends BasePage {

	@ApiModelProperty("用户id")
	private Integer userId;

	@ApiModelProperty("导师ID或导师名称")
	private String tutor;

	@ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;

	@ApiModelProperty("项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
	private Integer itemType;

	@ApiModelProperty(value = "状态，null-全部，0：审核中，1：通过，2拒绝")
	private Integer status;

	@ApiModelProperty(value = "申请时间，开始")
	private Date timeStart;

	@ApiModelProperty(value = "申请时间,结束")
	private Date timeEnd;
	
	@ApiModelProperty(value = "审核时间，开始")
	private Date operateTimeStart;
	
	@ApiModelProperty(value = "审核时间,结束")
	private Date operateTimeEnd;

	@ApiModelProperty(value = "操作人")
	private String operator;
}
