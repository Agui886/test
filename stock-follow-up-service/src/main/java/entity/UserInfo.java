package entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import utils.StringUtil;

/**
 * <p>
 * 会员信息表
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_info")
@ApiModel(value="UserInfo对象", description="会员信息表")
public class UserInfo extends Model<UserInfo> {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户id,登录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
    private Integer accountType;

    @ApiModelProperty(value = "会员等级id")
    private Integer levelId;

    @ApiModelProperty(value = "上级的ID，0表示没有上级")
    private Integer agentId;

    @ApiModelProperty(value = "总代理")
    private Integer generalAgentId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "证件号")
    private String certificateNumber;
    
    @ApiModelProperty(value = "是否允许推广")
    private Boolean promotionEnable;

    @ApiModelProperty(value = "推广码")
    private String promotionCode;

    @ApiModelProperty(value = "国家或地区编号")
    private String areaCode;

    @ApiModelProperty(value = "手号号码，可用于有登录")
    private String phone;

    @ApiModelProperty(value = "邮箱，可用于有登录")
    private String email;

    @ApiModelProperty(value = "登录密码")
    private String loginPwd;

    @ApiModelProperty(value = "交易、资金密码")
    private String fundPwd;

    @ApiModelProperty(value = "国家地区")
    private String region;

    @ApiModelProperty(value = "是否可登录")
    private Boolean loginEnable;

    @ApiModelProperty(value = "是否可跟投")
    private Boolean followEnable;

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

    @ApiModelProperty(value = "滞留金")
    private BigDecimal detentionAmt;

    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "最后登录ip")
    private String lastLoginIp;

    @ApiModelProperty(value = "最后登录地址详情")
    private String lastLoginAddress;
    
    @ApiModelProperty(value = "邀请码（上级用户-推广码）")
    private String regInvitationCode;
    
    @ApiModelProperty(value = "证件类型")
	private Integer certificateType;

    @ApiModelProperty(value = "不可提现金额")
    @TableField(exist = false)
    private BigDecimal unavailableWithdrawalAmt;
    
    @ApiModelProperty(value = "是否在线")
    @TableField(exist = false)
    private Boolean isOnline;
    
    @ApiModelProperty(value = "上级代理名称")
    @TableField(exist = false)
    private String agentName;
    
    @ApiModelProperty(value = "总代理名称")
    @TableField(exist = false)
    private String generalAgentName;
    
    @ApiModelProperty(value = "等级名称")
    @TableField(exist = false)
    private String levelName;
    
    @ApiModelProperty(value = "等级值")
    @TableField(exist = false)
    private Integer levelVal;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    public String getOperator() {
    	return "id:" + this.id + "(会员)";
    }
    
    //是否人脸认证
    public boolean isFaceVerify() {
    	if (StringUtil.isEmpty(this.getRealName()) || StringUtil.isEmpty(this.getCertificateNumber())) {
			return false;//未认证
		}
    	return true;//已认证
    }

}
