package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowPendingListSearchParamVO extends BasePage {
	
	@ApiModelProperty(value = "委托ID")
	private Integer id;
	
	@ApiModelProperty(value = "用户ID")
	private Integer userId;
	
	@ApiModelProperty(value = "会员等级id")
	private Integer levelId;
	
	@ApiModelProperty(value = "导师id")
    private Integer tutorId;
	
	@ApiModelProperty(value = "跟投记录ID")
	private Integer followRecordId;
	
	@ApiModelProperty(value = "跟投持仓id，转入持仓后关联")
    private Integer positionId;
	
	@ApiModelProperty(value = "信号ID")
	private Integer signId;
	
	@ApiModelProperty(value = "交易订单号")
    private String tradingOrderSn;
	
	@ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;
	
	@ApiModelProperty("项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
	private Integer itemType;
	
	@ApiModelProperty(value = "信号类型,null-全部,0-买入,1-卖出")
	private Integer signType;
	
	@ApiModelProperty("委托时间，开始")
	private Date pendingTimeStart;
	
	@ApiModelProperty("委托时间，结束")
	private Date pendingTimeEnd;
	
	@ApiModelProperty(value = "委托-状态，0:待成交，1：已完成")
	private Integer pendingStatus;
	
	@ApiModelProperty(value = "信号跟踪-状态，0:（信号-跟投中，1：信号-已跟上，2：信号-未跟上）")
    private Integer signTraceStatus;
	
	@ApiModelProperty(value = "用户跟投状态，0-进行中，1-顺延数据库不存储此状态，通过结束时间-当前时间，为负数时，显示顺延状态），2-已结束")
    private Integer followStatus;

}
