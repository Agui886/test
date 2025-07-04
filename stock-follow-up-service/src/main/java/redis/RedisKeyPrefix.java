package redis;

public class RedisKeyPrefix {

	public static final String SERVER = "SFU-SERVER:";

	public static final String MANAGER = "SFU-MANAGER:";
	
	//阿里云人脸认证
	public static final String ALIYUNFACEVERIFY = "ALIYUN-FACE-VERIFY:";

	public static String getRegisterValidCodeKey(String account) {
		return SERVER + "RegisterValidCode:" + account;
	}

	public static String getOnlineUserKey(Integer userId) {
		return SERVER + "onlineUser:" + userId;
	}

	public static String getTokenUserKey(String token) {
		return SERVER + token;
	}

	public static String getUserLoginFailedTimesKey(Integer userId) {
		return SERVER + "loginFailedTimes:" + userId;
	}

	public static String getServerRequestLimitKey(String token, String servletPath, String methodName) {
		return SERVER + "serverRequestLimit:" + token + ":" + servletPath + ":" + methodName;
	}
	
	public static String getAStockIndexQuotesKey() {
		return SERVER + "AStockIndexQuotesKey";
	}
	
	public static String getHkStockIndexQuotesKey() {
		return SERVER + "HkStockIndexQuotesKey";
	}
	
	public static String getUsStockIndexQuotesKey() {
		return SERVER + "UsStockIndexQuotesKey";
	}
	
	public static String getAStockIndustryPlateQuotesKey() {
		return SERVER + "AStockPlateIndustryQuotesKey";
	}
	
	public static String getAStockConceptPlateQuotesKey() {
		return SERVER + "AStockPlateConceptQuotesKey";
	}
	
	public static String getHkStockIndustryPlateQuotesKey() {
		return SERVER + "HkStockIndustryPlateQuotesKey";
	}
	
	public static String getUsStockPlateQuotesKey() {
		return SERVER + "UsStockPlateQuotesKey";
	}
	
	public static String getAStockHotRankKey() {
		return SERVER + "AStockHotRankKey";
	}

	public static String getHkStockHotRankKey() {
		return SERVER + "HkStockHotRankKey";
	}
	
	public static String getUsStockHotRankKey() {
		return SERVER + "UsStockHotRankKey";
	}
	
	public static String getAStockQuoteListKey() {
		return SERVER + "AStockQuoteList";
	}
	
	public static String getUsStockQuoteListKey() {
		return SERVER + "UsStockQuoteList";
	}
	public static String getHkStockQuoteListKey() {
		return SERVER + "HkStockQuoteList";
	}

	public static String getOnlineSysUserKey(Integer sysUserId) {
		return MANAGER + "onlineSysUser:" + sysUserId;
	}

	public static String getSysTokenKey(String token) {
		return MANAGER + "getSysTokenKey:" + token;
	}

	public static String getIpAddressKey(String ip) {
		return "IpAddress:" + ip;
	}

	public static String getCountryListKey() {
		return "Country";
	}

	public static String getSysParamConfigKey() {
		return "SysParamConfig";
	}

	public static String getPNextSupperUsersKey(Integer userId) {
		return "PNextSupperUsers:" + userId;
	}

	public static String getPNextSupperAgentsKey(Integer agentId) {
		return "PNextSupperAgents:" + agentId;
	}
	
	//获取阿里云人脸认证key
	public static String getAliyunInitFaceVerifyParamVOKey(String certifyId) {
		return ALIYUNFACEVERIFY + "AliyunInitFaceVerifyParamVO:" + certifyId;
	}
	
	public static String getAgentConfigKey() {
		return MANAGER + "AgentConfig";
	}
	
	//一只股票的当天的-分时成交记录
	public static String getStockDayTransactionKey(String stockCode, String stockType) {
		return MANAGER + "StockDayTransaction:"+stockType+stockCode;
	}
}
