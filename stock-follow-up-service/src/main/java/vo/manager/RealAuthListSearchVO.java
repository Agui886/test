package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RealAuthListSearchVO extends BasePage {

	private Integer userId;

	@ApiModelProperty(value = "申请时间，开始")
	private Date requestTimeStart;

	@ApiModelProperty(value = "申请时间，结束")
	private Date requestTimeEnd;

	@ApiModelProperty(value = "申请时间，开始")
	private Date operateTimeStart;

	@ApiModelProperty(value = "申请时间，结束")
	private Date operateTimeEnd;

	@ApiModelProperty(value = "null-查全部,状态（1：审核中，2：已通过，3：不通过）")
	private Integer realAuthStatus;
}
