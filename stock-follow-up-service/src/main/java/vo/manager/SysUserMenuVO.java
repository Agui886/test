package vo.manager;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserMenuVO {

	@ApiModelProperty(value = "管理员id")
	private Integer sysUserId;

	@ApiModelProperty(value = "菜单id")
	private Integer menuId;

	@ApiModelProperty(value = "直属上级菜单标识")
	private Integer parentId;

	@ApiModelProperty(value = "菜单名称")
	private String title;

	@ApiModelProperty(value = "菜单类型，0：页面，1：按钮或后端接口")
	private Integer menuType;

	@ApiModelProperty(value = "是否包含权限，打开权限配置时，该值为true时，打勾")
	private boolean hasAuth;

	@ApiModelProperty(value = "子菜单列表")
	@TableField(exist = false)
	private List<SysUserMenuVO> children;
}
