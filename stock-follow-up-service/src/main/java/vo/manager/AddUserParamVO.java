package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddUserParamVO {
	
	@ApiModelProperty("所属代理的ID")
	private Integer agentId;
	
	@ApiModelProperty("账户类型，实盘-0，模拟-1")
	private Integer accountType = 0;
	
	@ApiModelProperty("地区编号，如中国:+86")
	private String areaCode;
	
	@ApiModelProperty("手机号码")
	private String phone;
	
	@ApiModelProperty("邮箱")
	private String email;
	
	@ApiModelProperty("登录密码")
	private String loginPwd;
	
	@ApiModelProperty(value = "真实姓名")
    private String realName;
	
	@ApiModelProperty(value = "证件号")
    private String certificateNumber;
}
