package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowStatisticsSearchParamVO extends BasePage {

	@ApiModelProperty("用户Id")
	private Integer userId;
	
	@ApiModelProperty(value = "开始时间")
    private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
    private Date endTime;
	
}
