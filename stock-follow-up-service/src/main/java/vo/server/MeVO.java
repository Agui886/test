package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MeVO {

	@ApiModelProperty(value = "登录id、uid")
	private Integer loginId;

	@ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
	private Integer accountType;

	@ApiModelProperty(value = "昵称")
	private String nickname;
	
	@ApiModelProperty(value = "地区编号，如+86")
	private String areaCode;
	
	@ApiModelProperty(value = "手机号码")
	private String phone;
	
	@ApiModelProperty(value = "邮箱地址")
	private String email;
	
	@ApiModelProperty(value = "实名认证状态,0-未实名，1-审核中，2-已审核,3-未审核通过（被驳回）")
    private Integer realAuthStatus;
    
    @ApiModelProperty(value = "融资状态， 0未开启/1审核中/2审核通过/3审核拒绝")
    private Integer fundingStatus;
    
    @ApiModelProperty(value = "是否设置了交易密码")
    private Boolean hasFundPwd;

    @ApiModelProperty(value = "可用金额")
    private BigDecimal availableAmt;
    
    @ApiModelProperty(value = "交易中的冻结金额")
    private BigDecimal tradingFrozenAmt;

    @ApiModelProperty(value = "跟投资金")
    private BigDecimal followAmt;
    
    @ApiModelProperty(value = "可提现金额")
    private BigDecimal availableWithdrawalAmt;
    
    @ApiModelProperty(value = "滞留金")
    private BigDecimal detentionAmt;
    
    @ApiModelProperty(value = "未读信息数量，公告、站内信等")
    private int unReadMessages;
    
    @ApiModelProperty(value = "申请时间")
    private Date requestTime;
    
    @ApiModelProperty(value = "签名")
    private String signature;
    
    @ApiModelProperty(value = "会员等级id")
    private Integer levelId;
    
    @ApiModelProperty(value = "推广码")
    private String promotionCode;
    
    @ApiModelProperty(value = "是否允许推广")
    private Boolean promotionEnable;
}
