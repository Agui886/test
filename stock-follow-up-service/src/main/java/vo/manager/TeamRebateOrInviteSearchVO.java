package vo.manager;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TeamRebateOrInviteSearchVO {

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
	
	@ApiModelProperty("本级用户id")
	private Integer userId;
	
	@ApiModelProperty("本级昵称")
	private String nickname;
	
	@ApiModelProperty("本级等级值")
	private Integer levelVal; 
	
	@ApiModelProperty("本级等级名称")
	private String levelName;
	
	@ApiModelProperty("L1成员列表")
	private List<MemberInfo> membersOfL1 = new ArrayList<>();
	
	@ApiModelProperty("L2成员列表")
	private List<MemberInfo> membersOfL2 = new ArrayList<>();
	
	@ApiModelProperty("L3成员列表")
	private List<MemberInfo> membersOfL3 = new ArrayList<>();
	
	@ApiModelProperty("L4成员列表")
	private List<MemberInfo> membersOfL4 = new ArrayList<>();
	
	@ApiModelProperty("L5成员列表")
	private List<MemberInfo> membersOfL5 = new ArrayList<>();
	
	@Data
	public static class MemberInfo {
		
		@ApiModelProperty("用户id")
		private Integer userId;
		
		@ApiModelProperty("昵称")
		private String nickname;
		
		@ApiModelProperty("等级值")
		private Integer levelVal; 
		
		@ApiModelProperty("等级名称")
		private String levelName;
	}
}
