package entity.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasePage {

    @ApiModelProperty(name = "pageNo", value = "页码", required = true,example = "1")
    protected Integer pageNo = 1;

    @ApiModelProperty(name = "pageSize", value = "页面大小", required = true,example = "10")
    protected Integer pageSize = 10;

}
