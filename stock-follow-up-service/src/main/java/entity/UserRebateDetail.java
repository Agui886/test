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

/**
 * <p>
 * 用户返佣记录明细
 * </p>
 *
 * @author 
 * @since 2025-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_rebate_detail")
@ApiModel(value="UserRebateDetail对象", description="用户返佣记录明细")
public class UserRebateDetail extends Model<UserRebateDetail> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "贡献用户")
    private Integer fromUserId;

    @ApiModelProperty(value = "用户当时等级")
    private Integer userLevelVal;
    
    @ApiModelProperty(value = "用户当时等级名称")
    private String userLevelName;
    
    @ApiModelProperty(value = "贡献用户当时等级")
    private Integer fromUserLevelVal;

    @ApiModelProperty(value = "贡献用户当时等级名称")
    private String fromUserLevelName;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "返佣比例")
    private BigDecimal rebateRatio;

    @ApiModelProperty(value = "返佣金额")
    private BigDecimal rebateAmount;

    @ApiModelProperty(value = "业绩")
    private BigDecimal performance;
    
    @ApiModelProperty(value = "是否结算")
    private Boolean isSettlemented;
    
    @ApiModelProperty(value = "结算日期")
    private Date settlementDate;
    
    @ApiModelProperty(value = "结算时间")
    private Date settlementDateTime;
   
    @ApiModelProperty("返佣类型，0-跟投的买卖交易，1-用户个人买入股票买，2-用户个人卖了股票")
    private Integer rebateType;
    
    @ApiModelProperty("交易订单号")
    private String tradingOrderSn;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "贡献用户昵称")
    private String fromUserNickname;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
