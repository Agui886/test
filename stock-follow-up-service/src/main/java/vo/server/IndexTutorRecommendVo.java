package vo.server;

import java.util.List;

import entity.TutorInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IndexTutorRecommendVo {
	
	@ApiModelProperty("今日导师推荐")
	private FollowTutorListVO todayRecommend;
	
	@ApiModelProperty("每日优投")
	private FollowTutorListVO dailyPreferred;
	
	@ApiModelProperty("一键优投")
	private FollowTutorListVO oneClickPreferred;
	
}
