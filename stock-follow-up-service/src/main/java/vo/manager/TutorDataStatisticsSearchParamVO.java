package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TutorDataStatisticsSearchParamVO extends BasePage {

	@ApiModelProperty("导师ID")
	private Integer tutorId;
	
	@ApiModelProperty(value = "开始时间")
    private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
    private Date endTime;
	
}
