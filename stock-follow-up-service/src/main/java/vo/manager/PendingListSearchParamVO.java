package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PendingListSearchParamVO extends BasePage {
	
	@ApiModelProperty("持仓D")
	private Integer positionId;
	
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
	
	@ApiModelProperty("是否大宗交易，null-查全部，true-是,false-否")
	private Boolean isBlockTrading;
	
	@ApiModelProperty("委托时间，开始")
	private Date pendingTimeStart;
	
	@ApiModelProperty("委托时间，结束")
	private Date pendingTimeEnd;
	
	@ApiModelProperty(value = "持仓状态，null-查全部,1-已委托，2-已成交（正式持仓），3-已撤销，4-已拒绝")
    private Integer positionStatus;
}
