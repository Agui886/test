package vo.common;

import lombok.Data;

@Data
public class RequestLimitVO {
	
	private String servletPath;
	
	private String methodName;
	
	private long lastTimeMillis = System.currentTimeMillis();
}
