package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDataChangeRecordSearchParamVO extends BasePage {
	
	@ApiModelProperty("会员ID")
	private Integer userId;
	
	@ApiModelProperty("变更类型代码")
	private String userDataChangeTypeCode;
	
	@ApiModelProperty("变更时间，开始,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeStart;
	
	@ApiModelProperty("变更时间，结束,格式：yyyy-MM-dd HH:mm:ss")
	private Date createTimeEnd;
}
