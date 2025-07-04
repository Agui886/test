package vo.manager;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserListDetailVO {
	@ApiModelProperty(value = "用户id,登录id")
	private Integer id;
	@ApiModelProperty(value = "昵称")
	private String nickname;
	@ApiModelProperty(value = "国家或地区编号")
	private String areaCode;
	@ApiModelProperty(value = "手号号码，可用于有登录")
	private String phone;
	@ApiModelProperty(value = "邮箱，可用于有登录")
	private String email;
	@ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
	private Integer accountType;
	@ApiModelProperty(value = "是否可登录")
	private Boolean loginEnable;
	@ApiModelProperty(value = "是否可跟投")
	private Boolean followEnable;
	@ApiModelProperty(value = "上级的ID，0表示没有上级")
	private Integer agentId;
	@ApiModelProperty(value = "上级代理名称")
	private String agentName;
	@ApiModelProperty(value = "注册时间")
	private Date regTime;
	@ApiModelProperty(value = "注册的IP")
	private String regIp;
	@ApiModelProperty(value = "注册的ip地址详情")
	private String regAddress;
	@ApiModelProperty(value = "实名认证状态,0-未实名，1-审核中，2-已审核,3-未审核通过（被驳回）")
	private Integer realAuthStatus;
	@ApiModelProperty(value = "融资状态， 0未开启/1审核中/2审核通过/3审核拒绝")
	private Integer fundingStatus;
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
	
	@ApiModelProperty(value = "真实姓名")
    private String realName;
	@ApiModelProperty(value = "证件号")
    private String certificateNumber;
}
