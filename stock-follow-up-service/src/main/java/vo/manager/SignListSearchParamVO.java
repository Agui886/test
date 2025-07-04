package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SignListSearchParamVO extends BasePage {

	@ApiModelProperty("信号id")
	private Integer id;

	@ApiModelProperty("导师ID或导师名称")
	private String tutor;

	@ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;

	@ApiModelProperty(value = "项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
	private Integer itemType;

	@ApiModelProperty(value = "信号类型,null-全部,0-买入,1-卖出")
	private Integer signType;

	@ApiModelProperty(value = "是否已配置项目,null-全部，0-未配置,1-已配置")
    private Boolean isConfigured;

	@ApiModelProperty(value = "显示时间，开始")
	private Date displayTimeStart;

	@ApiModelProperty(value = "显示时间,结束")
	private Date displayTimeEnd;
	
	@ApiModelProperty(value = "实际时间，开始")
	private Date realTimeStart;

	@ApiModelProperty(value = "实际时间,结束")
	private Date realTimeEnd;

	@ApiModelProperty(value = "发布人")
	private String publisher;
}