package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserListSearchParamVO extends BasePage {
	
	@ApiModelProperty("会员ID，登录ID")
	private Integer id;
	
	@ApiModelProperty("账户类型，null全部，0-实盘，1-模拟")
	private Integer accountType;
	
	@ApiModelProperty("代理ID， null或0-全部")
	private Integer agentId;
	
	@ApiModelProperty("是否查询所选代理层的所有下级会员信息，否，则只查询所选的代理的直属下级会员")
	private boolean searchChildOfAgent;
	
	@ApiModelProperty("会员真实姓名")
	private String realName;
	
	@ApiModelProperty("注册邀请码")
	private String invitationCode;
	
	@ApiModelProperty("会员手机号")
	private String phone;
	
	@ApiModelProperty("会员邮箱")
	private String email;
	
	@ApiModelProperty("登录状态：null-查全部，true-可登录,false-不可登录")
	private Boolean loginEnable;
	
//	@ApiModelProperty("交易状态：null-查全部，true-可交易,false-不可交易")
//	private Boolean tradeEnable;
	
	@ApiModelProperty("实名认证状态,null-查全部，0-未实名，1-审核中，2-已审核,3-未通过")
	private Integer realAuthStatus;
	
	@ApiModelProperty(value = "融资状态，null-查全部/0未开启/1审核中/2审核通过/3审核拒绝")
    private Integer fundingStatus;
	
	@ApiModelProperty("注册时间，开始,格式：yyyy-MM-dd HH:mm:ss")
	private Date regTimeStart;
	
	@ApiModelProperty("注册时间，结束,格式：yyyy-MM-dd HH:mm:ss")
	private Date regTimeEnd;
	
	@ApiModelProperty("最后登录时间，开始,格式：yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTimeStart;
	
	@ApiModelProperty("最后登录时间，结束,格式：yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTimeEnd;
	
	@ApiModelProperty("排序方式:0-最后登录时间，1-注册时间，2-可用金额，默认0")
	private Integer orderBy;
	
	@ApiModelProperty("0-倒序，1-正序，默认0")
	private Integer descOrAsc;
}
