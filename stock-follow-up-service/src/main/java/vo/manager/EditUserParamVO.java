package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditUserParamVO {
	
	@ApiModelProperty("用户ID")
	private Integer userId;
	
	@ApiModelProperty("所禹代理id, null或0为不设置上级代理")
	private Integer agentId;
	
	@ApiModelProperty("地区编号")
	private String areaCode;
	
	@ApiModelProperty("电话号码")
	private String phone;
	
	@ApiModelProperty("邮箱账号")
	private String email;
	
	@ApiModelProperty("登录密码")
	private String loginPwd;
	
	@ApiModelProperty("资金、交易密码")
	private String fundPwd;
	
	@ApiModelProperty("是否可登录")
	private Boolean loginEnable;
	
	@ApiModelProperty(value = "是否可跟投")
	private Boolean followEnable;
}
