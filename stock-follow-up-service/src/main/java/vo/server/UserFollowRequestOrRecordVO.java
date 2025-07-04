package vo.server;

import java.math.BigDecimal;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import entity.UserFollowUpPending;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowRequestOrRecordVO{
	
	/**
	 * 审核记录
	 */
	@ApiModelProperty("跟投审核记录id")
	private Integer followRequestId;
	
	@ApiModelProperty(value = "申请时间")
	private Date requestTime;
	
	@ApiModelProperty(value = "跟投资金")
    private BigDecimal amount;
	
	@ApiModelProperty("项目跟投类型，null-全部，0：每日跟投，1：7日跟投，2：16日跟投，3：38日跟投，4：108日跟投，5：180日跟投，6：360日跟投")
    private Integer itemType;
	
	
	/**
	 * 跟投记录
	 */
	@ApiModelProperty("跟投记录id")
	private Integer followRecordId;
	
	@ApiModelProperty("跟投单号")
	private String followOrderSn;
	
	@ApiModelProperty(value = "开始-跟投时间")
    private Date followStartTime;
	
	@ApiModelProperty(value = "退还-跟投资金")
    private BigDecimal returnFollowSum;

	@ApiModelProperty(value = "导师-抽成")
    private BigDecimal tutorCommission;

    @ApiModelProperty(value = "平台-抽成")
    private BigDecimal platformCommission;
    
	/**
	 * 导师信息
	 */
	@ApiModelProperty(value = "导师id")
    private Integer tutorId;
	
	@ApiModelProperty("导师名称")
    private String tutorName;

}
