package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TutorChangeRecordListSearchParamVO extends BasePage {

	@ApiModelProperty("导师ID")
	private Integer tutorId;

	@ApiModelProperty(value = "操作类型代码，null-全部")
	private String dataChangeTypeCode;

	@ApiModelProperty(value = "修改人，操作人")
	private String operator;
	
	@ApiModelProperty(value = "时间，开始")
    private Date timeStart;
	
	@ApiModelProperty(value = "时间,结束")
    private Date timeEnd;
}
