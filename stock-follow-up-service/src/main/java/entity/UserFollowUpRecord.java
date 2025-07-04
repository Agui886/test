package entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import cn.hutool.core.date.DateUtil;
import enums.UserFollowStatusEnum;

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
 * 用户跟投记录表
 * </p>
 *
 * @author 
 * @since 2025-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_follow_up_record")
@ApiModel(value="UserFollowUpRecord对象", description="用户跟投记录表")
public class UserFollowUpRecord extends Model<UserFollowUpRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "导师id")
    private Integer tutorId;

    @ApiModelProperty(value = "项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;

    @ApiModelProperty(value = "开始-跟投时间")
    private Date followStartTime;

    @ApiModelProperty(value = "用户跟投状态，0-进行中，1-顺延数据库不存储此状态，通过结束时间-当前时间，为负数时，显示顺延状态），2-已结束")
    private Integer followStatus;

    @ApiModelProperty(value = "结束-跟投时间")
    private Date followEndTime;

    @ApiModelProperty(value = "跟投-结算时间")
    private Date followClosingTime;

    @ApiModelProperty(value = "初始-跟投资金")
    private BigDecimal initialFollowSum;

    @ApiModelProperty(value = "当前-跟投资金")
    private BigDecimal currentFollowSum;

    @ApiModelProperty(value = "退还-跟投资金")
    private BigDecimal returnFollowSum;

    @ApiModelProperty(value = "导师-抽成")
    private BigDecimal tutorCommission;

    @ApiModelProperty(value = "平台-抽成")
    private BigDecimal platformCommission;
    
    @ApiModelProperty(value = "用户跟投审核id")
    private Integer followRequestId;
    
    @ApiModelProperty(value = "跟投单号")
	private String followOrderSn;
    
    @ApiModelProperty(value = "当前-跟投总仓位数值比")
    private BigDecimal currentFollowPositionValueRatio;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "导师名称")
    private String tutorName;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "会员等级id")
	private Integer levelId;
    
    /**代理等级表的信息**/
    @ApiModelProperty(value = "等级名称")
    @TableField(exist = false)
    private String levelName;
    
    @ApiModelProperty(value = "等级值")
    @TableField(exist = false)
    private Integer levelVal;
    /**代理等级表的信息**/
    
    @TableField(exist = false)
    @ApiModelProperty(value = "域类型，null-全部，0-公开，1-私密")
	private Integer domainType;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "剩余天数")
    private Integer remainingDays;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    
    public Integer getRemainingDays() {
    	if (this.followEndTime != null) {
			return StringUtil.calculateDaysDifference(this.followEndTime);
		}
    	return null;
    }
    
    public Integer getFollowStatus() {
    	if (this.followStatus != null && this.followStatus == UserFollowStatusEnum.UNDERWAY.getCode() && getRemainingDays() < 0) {
    		return UserFollowStatusEnum.POSTPONE.getCode();
		}
    	return  followStatus;
    }

}
