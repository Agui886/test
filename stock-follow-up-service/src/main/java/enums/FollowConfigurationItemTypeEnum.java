package enums;

/**
 * 跟投项目类型-枚举
 * code 类型
 * value 结算天数值
 * name 名称
 */
public enum FollowConfigurationItemTypeEnum {
	EVERYDAY(0,0, "每日跟投"),
	DAY7(1,6, "7日跟投"),
	DAY16(2,15, "16日跟投"),
	DAY38(3,37, "38日跟投"),
	DAY108(4,107, "108日跟投"),
	DAY180(5,179, "180日跟投"),
	DAY360(6,359, "360日跟投");
	
	private int code;
	
	private int value;
	
	private String name;
	
	public int getCode() {
		return code;
	}
	
	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	FollowConfigurationItemTypeEnum(int code,int value, String name) {
		this.code = code;
		this.value = value;
		this.name = name;
	}

	/**
	 * 根据类型获取，名称
	 * @param code
	 * @return
	 */
	public static String getNameByCode(int code) {
		for (FollowConfigurationItemTypeEnum e : FollowConfigurationItemTypeEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
	
	/**
	 * 根据类型获取，结算天数值
	 * @param code
	 * @return
	 */
	public static int getVlueByCode(int code) {
		for (FollowConfigurationItemTypeEnum e : FollowConfigurationItemTypeEnum.values()) {
			if (code == e.getCode()) {
				return e.getValue();
			}
		}
		return 0;
	}
}
