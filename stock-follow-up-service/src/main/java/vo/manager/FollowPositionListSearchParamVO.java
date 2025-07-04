package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowPositionListSearchParamVO extends BasePage {
	
	@ApiModelProperty("持仓ID")
	private Integer id;
	
	@ApiModelProperty("用户ID")
	private Integer userId;
	
	@ApiModelProperty("导师id")
    private Integer tutorId;
	
	@ApiModelProperty("跟投记录ID")
	private Integer followRecordId;
	
	@ApiModelProperty(value = "持仓状态，0-正式持仓，1-已平仓")
    private Integer positionStatus;
	
	@ApiModelProperty("项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
	private Integer itemType;
	
	@ApiModelProperty("开始时间")
	private Date creatPositionTimeStart;
	
	@ApiModelProperty("结束时间")
	private Date creatPositionTimeEnd;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
}
