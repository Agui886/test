package vo.server;

import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowTutorListParamVO extends BasePage {
	
	@ApiModelProperty("客户端不用传-用户id")
	private Integer userId;
	
	@ApiModelProperty("推荐类型(0:公开,1:私密)")
	private Integer domainType;
	
	@ApiModelProperty("推荐类型(0:今日推荐,1:每日优投,2:一键优投)")
	private Integer recommendType;
	
	@ApiModelProperty("客户端不用传-是否可跟投(1:启用，0:禁止)")
	private Boolean isEnabled;
	 
	@ApiModelProperty(value = "私域邀请码")
    private String privateInvitationCode;
	

}
