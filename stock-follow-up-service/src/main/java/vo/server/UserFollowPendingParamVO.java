package vo.server;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import entity.common.BasePage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserFollowPendingParamVO extends BasePage {
	
	@ApiModelProperty("客户端不用传")
	private Integer userId;
	
	@ApiModelProperty("跟投记录id")
	private Integer followRecordId;
	
	@ApiModelProperty("显示发布时间类型，全部不传（1：今天，2：昨天）")
	private Integer displayReleaseTimeType;
	
	@ApiModelProperty(value = "显示发布时间-开始")
    private Date displayReleaseTimeStart15;
	
	@ApiModelProperty(value = "显示发布时间-结束")
    private Date displayReleaseTimeEnd15;
	
	@ApiModelProperty(value = "跟投持仓id，转入持仓后关联")
	private Integer positionId;
	
	//显示发布时间-开始（加15分钟）
	public void setDisplayReleaseTimeStart15(Date displayReleaseTimeStart15) {
		if(displayReleaseTimeStart15 != null) {
			this.displayReleaseTimeStart15 = DateUtil.offsetMinute(displayReleaseTimeStart15, 15);//查询时间+15分钟
		}
	}
	//显示发布时间-结束（加15分钟）
	public void setDisplayReleaseTimeEnd15(Date displayReleaseTimeEnd15) {
		if (displayReleaseTimeEnd15 != null) {
			this.displayReleaseTimeEnd15 = DateUtil.offsetMinute(displayReleaseTimeEnd15, 15);//查询时间+15分钟
		}
	}
}
