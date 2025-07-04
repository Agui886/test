package vo.manager;

import java.util.List;

import entity.SignInfo;
import entity.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowPendingTransferDetailVO {
	
	@ApiModelProperty(value = "信号信息")
	private SignInfo signInfo;
	
	@ApiModelProperty(value = "用户及跟投相关List信息")
	private List<FollowPendingTransferDetailUserVO> followPendingTransferDetailUserVO;
	
}
