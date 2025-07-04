package vo.server;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyInfoVO {
	
	@ApiModelProperty("uid")
	private Integer uid;
	
	@ApiModelProperty("姓名")
	private String realName;
	
	@ApiModelProperty("昵称")
	private String nickname;
	
	@ApiModelProperty(value = "证件类型")
	private Integer certificateType;
	
	@ApiModelProperty(value = "证件号码")
	private String certificateNumber;
	
	@ApiModelProperty(value = "实名认证状态,0-未实名，1-审核中，2-已审核,3-未审核通过（被驳回）")
    private Integer realAuthStatus;
    
    @ApiModelProperty(value = "融资状态， 0未开启/1审核中/2审核通过/3审核拒绝")
    private Integer fundingStatus;
    
    @ApiModelProperty(value = "推广码")
    private String promotionCode;
    
    @ApiModelProperty(value = "邀请码（上级用户-推广码）")
    private String regInvitationCode;
    
    
}
