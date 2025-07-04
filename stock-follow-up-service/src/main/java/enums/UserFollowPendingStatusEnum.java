package enums;

public enum UserFollowPendingStatusEnum {
	WAIT_COMPLETE(0, "待成交"),
	PROCESSED_COMPLETE(1, "已完成");
	
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserFollowPendingStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (UserFollowPendingStatusEnum e : UserFollowPendingStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
