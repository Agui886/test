package entity;

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
 * 用户等级升降记录
 * </p>
 *
 * @author 
 * @since 2025-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_level_change_record")
@ApiModel(value="UserLevelChangeRecord对象", description="用户等级升降记录")
public class UserLevelChangeRecord extends Model<UserLevelChangeRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "变更类型，-1-降级，1-升级")
    private Integer changeType;

    @ApiModelProperty(value = "变更前级别值")
    private Integer beLevelVal;

    @ApiModelProperty(value = "变更前级别名称")
    private String beLevelName;

    @ApiModelProperty(value = "变更后级别值")
    private Integer afLevelVal;

    @ApiModelProperty(value = "变更后级别名称")
    private String afLevelName;

    @ApiModelProperty(value = "变更时间")
    private Date createTime;
    
    
    @ApiModelProperty(value = "昵称")
    @TableField(exist = false)
    private String nickname;
    
    @ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
    @TableField(exist = false)
    private Integer accountType;

    @ApiModelProperty(value = "会员等级id")
    @TableField(exist = false)
    private Integer levelId;
    
    @ApiModelProperty(value = "等级名称")
    @TableField(exist = false)
    private String levelName;
    
    @ApiModelProperty(value = "等级值")
    @TableField(exist = false)
    private Integer levelVal;
    
    @ApiModelProperty(value = "上级代理名称")
    @TableField(exist = false)
    private String agentName;
    
    @ApiModelProperty(value = "总代理名称")
    @TableField(exist = false)
    private String generalAgentName;
    
    @ApiModelProperty(value = "上级的ID，0表示没有上级")
    @TableField(exist = false)
    private Integer agentId;

    @ApiModelProperty(value = "总代理")
    @TableField(exist = false)
    private Integer generalAgentId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
