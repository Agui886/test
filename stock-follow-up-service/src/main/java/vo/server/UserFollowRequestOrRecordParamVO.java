package vo.server;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowRequestOrRecordParamVO extends BasePage {
	
	@ApiModelProperty("搜索类型(0：待审核，1：优投中，2：已驳回，3：已结束)")
	private Integer searchType;
	
	@ApiModelProperty("客户端不用传")
	private Integer userId;
	
	@ApiModelProperty(value = "导师id")
    private Integer tutorId;
}
