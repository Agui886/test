package vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TypeVO {
	
	@ApiModelProperty("类型编码，用于传参")
	private String code;
	@ApiModelProperty("类型名称，用于显示")
	private String name;
}
