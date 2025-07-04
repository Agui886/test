package vo.manager;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserMenuSaveParamVO {
	
	@ApiModelProperty(value = "管理员id")
	private Integer sysUserId;

	@ApiModelProperty(value = "选择的菜单id")
	private List<Integer> menuIdList;
}
