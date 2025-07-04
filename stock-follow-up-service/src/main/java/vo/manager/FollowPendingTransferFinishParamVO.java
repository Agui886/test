package vo.manager;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowPendingTransferFinishParamVO {
	
	@ApiModelProperty(value = "委托订单-待转完成-详情,List")
	private List<FollowPendingTransferDetailUserVO> followPendingTransferDetailUserVOList;
	
	@ApiModelProperty(value = "信号id")
    private Integer signId;
	
	@ApiModelProperty(value = "交易仓位，比如0.25为1/4仓，0.5为1/2仓，1为全仓")
    private BigDecimal tradingPosition;
	
}