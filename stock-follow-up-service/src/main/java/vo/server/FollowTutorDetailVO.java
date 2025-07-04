package vo.server;

import java.math.BigDecimal;
import java.util.List;

import entity.TutorInfo;
import entity.UserFollowUpRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowTutorDetailVO extends TutorInfo{

	@ApiModelProperty(value = "总收益")
	private BigDecimal totalYield;
	
//	@ApiModelProperty(value = "月收益")
//	private BigDecimal monthYield;

	@ApiModelProperty(value = "仓位率")
	private BigDecimal positionRate;

//	@ApiModelProperty(value = "胜率")
//	private BigDecimal winRate;
	
	@ApiModelProperty(value = "上月-信号跟随-人数（用户去重）")
	private Integer lastMonthSignTraceUser;
	
	@ApiModelProperty(value = "上月-信号跟随-卖出总次数")
	private Integer lastMonthSignTraceSaleTotal ;
	
	@ApiModelProperty(value = "用户项目跟投状态（0:未跟投;1:审核中;2:跟投进行中）")
	private Integer userItemTypeFollowStatus;
	
	@ApiModelProperty(value = "用户跟投记录")
	private List<UserFollowRecordVO> userFollowRecordVOList;
	
	@ApiModelProperty(value = "用户审核记录")
	private List<UserFollowUpRequest> userFollowRequest;
	
	@ApiModelProperty(value = "近期买入信号记录")
	private List<FollowTutorDetailSignInfoVO> followTutorDetailSignInfoVO;

}
