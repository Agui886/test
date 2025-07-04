package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 后台账号登录信息表
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_login_log")
@ApiModel(value="SysUserLoginLog对象", description="后台账号登录信息表")
public class SysUserLoginLog extends Model<SysUserLoginLog> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "管理员标识（取自sys_user.id）")
    private Integer sysUserId;

    @ApiModelProperty(value = "管理员账号（取自sys_user.user_name）")
    private String sysUserName;

    @ApiModelProperty(value = "登录时间")
    private Date loginTime;

    @ApiModelProperty(value = "登录IP")
    private String loginIp;

    @ApiModelProperty(value = "IP所在地")
    private String ipAddress;

    @ApiModelProperty(value = "管理员名称")
    private String sysRealName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
