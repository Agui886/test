package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 会员登录日志表
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_login_log")
@ApiModel(value="UserLoginLog对象", description="会员登录日志表")
public class UserLoginLog extends Model<UserLoginLog> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员id")
    private Integer userId;

    @ApiModelProperty(value = "登录类型，0-登录，1-登出")
    private Integer loginType;

    @ApiModelProperty(value = "操作时间")
    @TableField("operateTime")
    private Date operateTime;

    @ApiModelProperty(value = "操作ip")
    private String ip;

    @ApiModelProperty(value = "Ip所在地详情")
    private String ipAddress;

    @ApiModelProperty(value = "操作状态，0-成功，1-密码错误，2-失败")
    private Integer operateStatus;

    @ApiModelProperty(value = "请求的链接")
    private String requestUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
