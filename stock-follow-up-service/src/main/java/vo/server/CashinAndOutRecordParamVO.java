package vo.server;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CashinAndOutRecordParamVO extends BasePage {
	
	@ApiModelProperty("客户端不用传")
	private Integer userId;
	
	@ApiModelProperty("查询日期，格式必须是yyyy-MM-dd HH:mm:ss，如2025年1月1号，2025-01-01 00:00:00")
	private Date searchDate;
	
	@ApiModelProperty("订单状态（0：待审核，1：已通过，2：已取消）")
	private Integer orderStatus;
	
	@ApiModelProperty(value = "开始时间")
	private Date startTime;
	
	@ApiModelProperty(value = "结束时间")
	private Date endTime;
}
