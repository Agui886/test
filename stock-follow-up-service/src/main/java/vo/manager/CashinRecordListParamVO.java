package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CashinRecordListParamVO extends BasePage {
	
	@ApiModelProperty("记录id")
	private Integer id;
	
	@ApiModelProperty(value = "会员标识（取自user.id）")
	private Integer userId;

	@ApiModelProperty(value = "代理id")
	private Integer agentId;

	@ApiModelProperty(value = "交易号，订单号")
	private String orderSn;

	@ApiModelProperty(value = "充值方式编码")
	private String cashinTypeCode;

	@ApiModelProperty(value = "订单状态（0：待审核，1：已通过，2：已取消）")
	private Integer orderStatus;

	@ApiModelProperty(value = "申请时间，开始")
	private Date requestTimeStart;

	@ApiModelProperty(value = "申请时间，结束")
	private Date requestTimeEnd;

	@ApiModelProperty(value = "审核时间，开始")
	private Date operateTimeStart;
	
	@ApiModelProperty(value = "审核时间，结束")
	private Date operateTimeEnd;
}
