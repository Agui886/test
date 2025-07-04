package entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

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
 * 用户跟投委托表
 * </p>
 *
 * @author 
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_follow_up_pending")
@ApiModel(value="UserFollowUpPending对象", description="用户跟投委托表")
public class UserFollowUpPending extends Model<UserFollowUpPending> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "导师id")
    private Integer tutorId;

    @ApiModelProperty(value = "信号id")
    private Integer signId;

    @ApiModelProperty(value = "跟投记录id")
    private Integer followRecordId;

    @ApiModelProperty(value = "跟投持仓id，转入持仓后关联")
    private Integer positionId;

    @ApiModelProperty(value = "委托-状态，0:待成交，1：已完成")
    private Integer pendingStatus;

    @ApiModelProperty(value = "委托-时间")
    private Date pendingTime;

    @ApiModelProperty(value = "操作-时间")
    private Date operationTime;
    
    @ApiModelProperty(value = "信号跟踪-状态，0:（信号-跟投中，1：信号-已跟上，2：信号-未跟上）")
    private Integer signTraceStatus;

    /**用户表的信息**/
    @TableField(exist = false)
    @ApiModelProperty(value = "用户等级id")
    private String levelId;
    /**用户表的信息**/
    
    /**导师表的信息**/
    @TableField(exist = false)
    @ApiModelProperty(value = "导师名称")
    private String tutorName;
    /**导师表的信息**/
    
    /**跟投表的信息**/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束-跟投时间")
    private Date followEndTime;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "用户跟投状态，0-进行中，1-顺延数据库不存储此状态，通过结束时间-当前时间，为负数时，显示顺延状态），2-已结束")
    private Integer followStatus;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "剩余天数")
    private Integer remainingDays;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "当前-跟投资金")
    private BigDecimal currentFollowSum;
    /**跟投表的信息**/
    
    /**信号表的信息**/
    @TableField(exist = false)
    @ApiModelProperty(value = "项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "信号类型,0-买入,1-卖出")
    private Integer signType;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "股票名称")
    private String stockName;

    @TableField(exist = false)
    @ApiModelProperty(value = "交易股票代码")
    private String stockCode;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "信号-仓位，比如0.25为1/4仓，0.5为1/2仓，1为全仓")
    private BigDecimal position;

    @TableField(exist = false)
    @ApiModelProperty(value = "信号-指导价")
    private BigDecimal price1;

    @TableField(exist = false)
    @ApiModelProperty(value = "信号-一手的指导价")
    private BigDecimal price2;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "显示发布时间")
    private Date displayReleaseTime;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "是否已配置项目")
    private Boolean isConfigured;
    /**信号表的信息**/
    
    /**交易表的信息**/
    @TableField(exist = false)
    @ApiModelProperty(value = "交易订单号")
    private String tradingOrderSn;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "完成-时间")
    private Date finishTime;
    /**交易表的信息**/
    
    /**代理等级表的信息**/
    @ApiModelProperty(value = "等级名称")
    @TableField(exist = false)
    private String levelName;
    
    @ApiModelProperty(value = "等级值")
    @TableField(exist = false)
    private Integer levelVal;
    /**代理等级表的信息**/
    
    
    @TableField(exist = false)
    @ApiModelProperty(value = "卖出的实时持有-股数（查UserFollowUpPending委托表，"
    		+ "根据委托单的用户和，当前委托单的买入信号，查出对应的买入委托单"
    		+ "关联买入委托单的持仓id，获取，holdShares持仓数")
    private Integer realTimeHoldShares;
    
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
