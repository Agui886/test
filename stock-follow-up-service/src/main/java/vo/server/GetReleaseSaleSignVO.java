package vo.server;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import entity.UserFollowUpPending;
import entity.UserFollowUpRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetReleaseSaleSignVO {

	@ApiModelProperty(value = "跟投记录list")
	private List<UserFollowUpRecord> userFollowUpRecordList;
	
	@ApiModelProperty(value = "可跟随-(跟投记录list）")
	private List<UserFollowUpRecord> yestUserFollowUpRecord;
	
	@ApiModelProperty(value = "无法跟随-(跟投记录list)")
	private List<UserFollowUpRecord> notUserFollowUpRecordList;

	public List<UserFollowUpRecord> getNotUserFollowUpRecordList() {
	    // 1. 安全处理待跟随列表
	    Set<Integer> pendingUserIds = Optional.ofNullable(this.yestUserFollowUpRecord)
	            .orElseGet(Collections::emptyList)
	            .stream()
	            .filter(Objects::nonNull)  // 过滤空对象
	            .map(UserFollowUpRecord::getUserId)
	            .filter(Objects::nonNull)  // 过滤空userId
	            .collect(Collectors.toSet());

	    // 2. 安全处理跟投记录列表
	    List<UserFollowUpRecord> safeRecordList = Optional.ofNullable(this.userFollowUpRecordList)
	            .orElseGet(Collections::emptyList)
	            .stream()
	            .filter(Objects::nonNull)  // 过滤空对象
	            .filter(record -> record.getUserId() != null)  // 确保userId非空
	            .collect(Collectors.toList());

	    // 3. 筛选不在待跟随列表中的记录
	    return safeRecordList.stream()
	            .filter(record -> !pendingUserIds.contains(record.getUserId()))
	            .collect(Collectors.toList());
	     
	}
}
