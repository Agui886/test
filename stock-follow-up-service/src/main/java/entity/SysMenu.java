package entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单信息表
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_menu")
@ApiModel(value="SysMenu对象", description="菜单信息表")
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "菜单名称")
    private String title;

    @ApiModelProperty(value = "直属上级菜单标识")
    private Integer parentId;

    @ApiModelProperty(value = "前端菜单URL")
    private String path;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "菜单类型，0：页面，1：按钮或后端接口")
    private Integer menuType;

    @ApiModelProperty(value = "请求后端接口")
    private String ajax;

    @ApiModelProperty(value = "菜单创建人")
    private String creattor;

    @ApiModelProperty(value = "排序值，值越小，排序优先级越高")
    private Integer sort;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "前端路由地址")
    private String routing;

    @ApiModelProperty(value = "子菜单列表")
    @TableField(exist = false)
    private List<SysMenu> children;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
