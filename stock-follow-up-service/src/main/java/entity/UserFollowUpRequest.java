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
 * 用户跟投申请表
 * </p>
 *
 * @author 
 * @since 2025-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_follow_up_request")
@ApiModel(value="UserFollowUpRequest对象", description="用户跟投申请表")
public class UserFollowUpRequest extends Model<UserFollowUpRequest> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;
    
    @ApiModelProperty(value = "导师id")
    private Integer tutorId;

    @ApiModelProperty("项目跟投类型，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;

    @ApiModelProperty(value = "跟投资金")
    private BigDecimal amount;

    @ApiModelProperty(value = "申请时间")
    private Date requestTime;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    @ApiModelProperty(value = "状态，0-审核中，1-通过，2-拒绝")
    private Integer status;

    @ApiModelProperty(value = "操作人")
    private String operator;
    
    @ApiModelProperty(value = "跟投申请单号")
    private String followRequestOrderSn;
    
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
