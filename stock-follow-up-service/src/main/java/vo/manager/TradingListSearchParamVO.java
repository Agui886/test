package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TradingListSearchParamVO extends BasePage {
	
	@ApiModelProperty(value = "交易ID")
	private Integer id;
	
	@ApiModelProperty(value = "用户ID")
	private Integer userId;
	
	@ApiModelProperty(value = "持仓ID")
	private Integer positionId;
	
	@ApiModelProperty(value = "跟投记录ID")
	private Integer followRecordId;
	
	@ApiModelProperty(value = "交易订单号")
	private String tradingOrderSn;
	
	@ApiModelProperty(value = "导师id")
    private Integer tutorId;
	
	@ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;
	
	@ApiModelProperty("项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
	private Integer itemType;
	
	@ApiModelProperty(value = "信号类型,null-全部,0-买入,1-卖出")
	private Integer signType;
	
	@ApiModelProperty(value = "股票名称")
	private String stockName;

	@ApiModelProperty(value = "股票代码")
	private String stockCode;
	
	@ApiModelProperty("交易时间，开始")
	private Date tradingTimeStart;
	
	@ApiModelProperty("交易时间，结束")
	private Date tradingTimeEnd;
	
	@ApiModelProperty(value = "操作人")
    private String operator;
	
	@ApiModelProperty("股票代码或名称")
	private String stockCodeOrName;
	
}
