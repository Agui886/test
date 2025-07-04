package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowRecordParamVO extends BasePage {
	
	@ApiModelProperty("跟投ID")
	private Integer id;
	
	@ApiModelProperty(value = "用户ID")
	private Integer userId;
	
	@ApiModelProperty(value = "导师id")
    private Integer tutorId;
	
	@ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;
	
	@ApiModelProperty("项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
	private Integer itemType;
	
	@ApiModelProperty("开始-跟投时间")
	private Date startFollowStartTime;
	
	@ApiModelProperty("结束-跟投时间")
	private Date endFollowStartTime;
	
	@ApiModelProperty("开始-跟投结算时间")
	private Date startFollowClosingTime;
	
	@ApiModelProperty("结束-跟投结算时间")
	private Date endFollowClosingTime;
	
	@ApiModelProperty(value = "用户跟投状态，0-进行中，(1-顺延数据库不存储此状态，通过结束时间-当前时间，为负数时，显示顺延状态,查询给1无效），2-已结束")
    private Integer followStatus;
	
}
