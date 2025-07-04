package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import entity.SignInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowTutorDetailSignInfoVO extends SignInfo{

	@ApiModelProperty(value = "卖出-信号id（最后一次）")
	private Integer saleSignInfoId;
	
	@ApiModelProperty(value = "卖出-指导价（最后一次）")
	private BigDecimal salePrice1;

	@ApiModelProperty(value = "卖出-显示发布时间（最后一次）")
	private Date saledisplayReleaseTime;

}
