package enums;

public enum UserFollowStatusEnum {
	UNDERWAY(0, "进行中"),
	POSTPONE(1, "顺延"),
	FINISH(2, "已结束");
	
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserFollowStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (UserFollowStatusEnum e : UserFollowStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
