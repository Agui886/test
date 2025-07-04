package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PositionChangeParamVO extends BasePage {
	
	@ApiModelProperty("用户ID")
	private Integer userId;
	
	@ApiModelProperty("持仓ID")
	private Integer positionId;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
	
	@ApiModelProperty("变更类型代码")
	private String dataChangeTypeCode;
	
	@ApiModelProperty("开始时间")
	private Date startTime;
	
	@ApiModelProperty("结束时间")
	private Date endTime;
}
