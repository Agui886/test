package vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vo.common.Members;

@Data
public class TeamDetailSearchVO {

	@ApiModelProperty("总人数")
	private int total;
	
	@ApiModelProperty("L1人数")
	private int totalOfL1;
	
	@ApiModelProperty("L2人数")
	private int totalOfL2;
	
	@ApiModelProperty("L3人数")
	private int totalOfL3;
	
	@ApiModelProperty("L4人数")
	private int totalOfL4;
	
	@ApiModelProperty("L5人数")
	private int totalOfL5;
	
	@ApiModelProperty("成员详情")
	private Members members = new Members();
}
