package vo.server;

import entity.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AliyunInitFaceVerifyParamVO {

	@ApiModelProperty(value = "MetaInfo环境参数")
	private String metaInfo;
	
	@ApiModelProperty(value = "客户业务页面回跳的目标地址")
	private String returnUrl;
	
    @ApiModelProperty(value = "认证类型:[0-注册人脸认证(未注册用户，注册+人脸认证)，"
    		+ "1-忘记密码(手机号-人脸认证,通过手机号，获取用户人脸认证)，"
    		+ "2-忘记密码(邮箱-人脸认证,通过邮箱，获取用户人脸认证)，"
    		+ "3-用户人脸认证(已注册用户，通过当前token用户人脸认证)]")
    private Integer verifyType;
    
    @ApiModelProperty(value = "用户对象-系统处理[(verifyType=1时，account、areaCode，必须设置),"
    		+ "(verifyType=2时,account必须设置),"
    		+ "(verifyType=3时,必须登录)]")
    private UserInfo userInfo;
    
    @ApiModelProperty(value = "认证通过状态-系统处理（0-发起认证，1-认证成功，2-认证失败）")
    private Integer verifyPassState = 0;
    
    @ApiModelProperty(value = "注册参数[verifyType=0时，必须设置]")
    private RegisterParamVO registerParamVO;
    
    @ApiModelProperty(value = "IP地址-系统处理[verifyType=0时，有效]")
    private String ip;
    
    @ApiModelProperty(value = "请求地址-系统处理[verifyType=0时，有效]")
    private String requestUrl;
    
    @ApiModelProperty(value = "忘记密码账号[(verifyType=1,必须设置手机号),"
    		+ "(verifyType=2,必须设置邮箱)]")
    private String account;
    
    @ApiModelProperty(value = "IP地址[verifyType=1时,必须设置国家或地区编号]")
    private String areaCode;
    
   
    
    
    
    
    
    
}
