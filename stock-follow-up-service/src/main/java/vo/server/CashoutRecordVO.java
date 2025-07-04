package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CashoutRecordVO {
	
	@ApiModelProperty(value = "提现订单id")
	private Integer id;

	@ApiModelProperty(value = "交易号，订单号")
	private String orderSn;

	@ApiModelProperty(value = "入款类型名称")
	private String cashoutTypeName;

	@ApiModelProperty(value = "银行卡号")
	private String cardNo;

	@ApiModelProperty(value = "银行名称")
	private String bankName;

	@ApiModelProperty(value = "申请金额")
	private BigDecimal orderAmount;
	
	@ApiModelProperty(value = "到账金额")
	private BigDecimal finalAmount;

	@ApiModelProperty(value = "申请时间")
	private Date requestTime;

	@ApiModelProperty(value = "订单状态（0：待审核，1：已通过，2：已取消）")
	private Integer orderStatus;
}
