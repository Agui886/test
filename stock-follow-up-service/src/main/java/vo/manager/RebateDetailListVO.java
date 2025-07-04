package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RebateDetailListVO {
	
	@ApiModelProperty("会员id")
	private Integer userId;
	
	@ApiModelProperty("会员昵称")
	private String nickname;
	
	@ApiModelProperty("会员等级")
	private Integer userLevelVal;
	
	@ApiModelProperty("会员等级名称")
	private String userLevelName;
	
	@ApiModelProperty("贡献会员id")
	private Integer fromUserId;
	
	@ApiModelProperty("贡献会员昵称")
	private String fromUserNickname;
	
	@ApiModelProperty("贡献会员等级")
	private Integer fromUserLevelVal;
	
	@ApiModelProperty("贡献会员等级名称")
	private String fromUserLevelName;

	@ApiModelProperty("贡献会员直属代理id")
	private Integer fromUserAgentId;
	
	@ApiModelProperty("贡献会员直属代理名称")
	private String fromUserAgentName;

	@ApiModelProperty("贡献会员总代理id")
	private Integer fromUserGeneralAgentId;
	
	@ApiModelProperty("贡献会员总代理名称")
	private String fromUserGeneralAgentName;
	
	@ApiModelProperty("业绩")
	private BigDecimal performance;
	
	@ApiModelProperty(value = "返佣比例")
    private BigDecimal rebateRatio;

    @ApiModelProperty(value = "返佣金额")
    private BigDecimal rebateAmount;
    
    @ApiModelProperty(value = "结算日期")
    private Date settlementDate;
    
    @ApiModelProperty(value = "结算时间")
    private Date settlementDateTime;
    
    @ApiModelProperty("返佣类型，0-跟投的买卖交易，1-用户个人买入股票买，2-用户个人卖了股票")
    private Integer rebateType;
    
    @ApiModelProperty("交易订单号")
    private String tradingOrderSn;
	
}
