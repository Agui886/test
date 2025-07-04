package vo.manager;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserLoginLogSearchParamVO extends BasePage {

	@ApiModelProperty(value = "管理员id")
	private Integer sysUserId;

	@ApiModelProperty(value = "管理员账号")
	private String sysUserName;

	@ApiModelProperty("有登录IP")
	private String loginIp;

	@ApiModelProperty("开始时间")
	private Date startTime;

	@ApiModelProperty("结束时间")
	private Date endTime;

}
