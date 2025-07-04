package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserLoginResponseVO {
	
	@ApiModelProperty(value = "令牌，放于接口的请求头:Authorization")
	private String token;
	
	@ApiModelProperty(value = "管理员账号")
	private String username;
	
	@ApiModelProperty(value = "管理员名称")
	private String realName;
}
