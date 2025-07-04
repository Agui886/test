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
 * 后台系统ip白名单表
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_ip_whitelist")
@ApiModel(value="SysIpWhitelist对象", description="后台系统ip白名单表")
public class SysIpWhitelist extends Model<SysIpWhitelist> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "ipv4地址")
    private String ipv4Val;

    @ApiModelProperty(value = "添加时间")
    private Date addDateTime;

    @ApiModelProperty(value = "该IP最后登录时间")
    private Date lastLoginTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
