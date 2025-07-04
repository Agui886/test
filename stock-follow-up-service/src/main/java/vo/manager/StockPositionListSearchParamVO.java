package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockPositionListSearchParamVO extends BasePage {
	
	@ApiModelProperty("持仓ID")
	private Integer id;
	
	@ApiModelProperty("代理ID")
	private Integer agentId;
	
	@ApiModelProperty("用户ID")
	private Integer userId;
	
	@ApiModelProperty("交易订单号")
	private String tradingOrderSn;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
	
	@ApiModelProperty("股票类型，sh-泸股，sz-深股，bj-北证，hk-港股，us-美股")
	private String stockType;
	
	@ApiModelProperty(value = "板块名称，如：科创、创业")
	private String stockPlate;
	
	@ApiModelProperty("开始时间")
	private Date startTime;
	
	@ApiModelProperty("结束时间")
	private Date endTime;
}
