package vo.server;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.UserRebateDetail;
import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vo.manager.RebateStatisticsVO.RebateStatistics;

@Data
public class UserRebateDetailVO extends BasePage {
	
	@ApiModelProperty("返佣列表分页数据")
	private Page<UserRebateDetail> page;
	
	@ApiModelProperty(value = "返佣统计")
	private RebateStatistics rebateStatistics;
	
}
