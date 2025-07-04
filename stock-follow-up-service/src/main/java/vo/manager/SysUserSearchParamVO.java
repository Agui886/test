package vo.manager;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserSearchParamVO extends BasePage {
	@ApiModelProperty(value = "管理员ID")
	private Integer id;

	@ApiModelProperty(value = "用户名")
	private String username;

	@ApiModelProperty(value = "用户名称")
	private String realName;
}
