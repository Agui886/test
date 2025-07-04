package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CashinRecordVO {

	@ApiModelProperty(value = "交易号，订单号")
	private String orderSn;

	@ApiModelProperty(value = "入款类型名称")
	private String cashinTypeName;

	@ApiModelProperty(value = "到账金额")
	private BigDecimal finalAmount;

	@ApiModelProperty(value = "申请时间")
	private Date requestTime;

	@ApiModelProperty(value = "订单状态（0：待审核，1：已通过，2：已取消）")
	private Integer orderStatus;
}
