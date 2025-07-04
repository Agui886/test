package vo.common;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class OnlineUserVO {
	
	private String latestToken;
	
	private Long currentTimestamp;
	
	private Set<String> tokenList = new HashSet<>();
}
