package vo.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TokenUserVO {
	
    @ApiModelProperty(value = "令牌，放于接口的请求头:Authorization")
	private String token;
	
    @ApiModelProperty(value = "登录id")
	private Integer loginId;

    @ApiModelProperty(value = "账户类型，实盘-0，模拟-1")
	private Integer accountType;
    
    @JsonIgnore
    public String getOperator() {
    	return "id:" + this.loginId + "(会员)";
    }
}
