package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserEditVO {
	@ApiModelProperty(value = "管理员ID")
	private Integer id;

	@ApiModelProperty(value = "管理员账号")
	private String username;

	@ApiModelProperty(value = "管理员名称")
	private String realName;

	@ApiModelProperty(value = "登录密码")
	private String loginPwd;

	@ApiModelProperty(value = "管理员状态（0：停用，锁定，1：启用）")
	private Integer userStatus;

}
