package vo.server;

import java.util.Date;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRebateDetailParamVO extends BasePage {
	
	@ApiModelProperty("客户端不用传")
	private Integer userId;

	@ApiModelProperty("贡献用户")
	private Integer fromUserId;
	
	@ApiModelProperty("结算日期-开始")
    private Date settlementDateStart;
	
	@ApiModelProperty("结算日期-结束")
    private Date settlementDateEnd;
	
}
