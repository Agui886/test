package vo.server;

import java.math.BigDecimal;

import entity.UserFollowUpRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowRecordVO extends UserFollowUpRecord{

	@ApiModelProperty(value = "跟投持仓市值")
	private BigDecimal positionFollowSum;

}
