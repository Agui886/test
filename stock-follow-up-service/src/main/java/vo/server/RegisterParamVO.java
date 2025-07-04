package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterParamVO {

	@ApiModelProperty("地区编号，如：中国，+86，必填")
	private String areaCode;

	@ApiModelProperty(value = "手号号码，可用于有登录，必填")
	private String phone;

	@ApiModelProperty(value = "邮箱，可用于有登录， 选填")
	private String email;
	
	@ApiModelProperty(value = "邀请码， 选填")
	private String invitationCode;
	
	@ApiModelProperty(value = "登录密码，长度8～12个字符，且必须包含数字和字母")
	private String loginPwd;
	
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    
    @ApiModelProperty(value = "证件号")
    private String certificateNumber;
}
