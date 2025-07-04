package enums;

public enum UserFollowPendingSignTraceStatusEnum {
	UNDERWAY(0, "信号-跟投中"),
	FOLLOW_UP(1, "信号-已跟上"),
	NOT_FOLLOW(2, "信号-未跟上");
	
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserFollowPendingSignTraceStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (UserFollowPendingSignTraceStatusEnum e : UserFollowPendingSignTraceStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
