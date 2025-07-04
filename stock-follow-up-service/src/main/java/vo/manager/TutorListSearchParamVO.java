package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TutorListSearchParamVO extends BasePage {

	@ApiModelProperty("导师ID或导师名称")
	private String tutor;

	@ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;

	@ApiModelProperty(value = "是否可跟投，null-全部，1-启用，0-禁止")
	private Boolean isEnabled;
	
	@ApiModelProperty(value = "创建时间，开始")
    private Date createTimeStart;
	
	@ApiModelProperty(value = "创建时间,结束")
    private Date createTimeEnd;

    @ApiModelProperty(value = "创建人")
    private String creator;
}
