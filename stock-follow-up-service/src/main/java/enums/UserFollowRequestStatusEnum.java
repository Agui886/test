package enums;

public enum UserFollowRequestStatusEnum {
	AUDIT(0, "审核中"),
	PASS(1, "通过"),
	REFUSE(2, "拒绝");
	
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserFollowRequestStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (UserFollowRequestStatusEnum e : UserFollowRequestStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
