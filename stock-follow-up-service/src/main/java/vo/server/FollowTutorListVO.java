package vo.server;

import entity.TutorInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FollowTutorListVO extends TutorInfo{

	@ApiModelProperty(value = "跟投状态(null:不处理，0:进行中，1:顺延，3:已结束)")
	private Integer followState;

    

}
