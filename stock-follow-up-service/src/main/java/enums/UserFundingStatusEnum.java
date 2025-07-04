package enums;

public enum UserFundingStatusEnum {
	NOT(0, "未认证"),
	APPLYING(1, "审核中"),
	REVIEWED(2, "已审核"),
	FAILED(3, "未审核通过");
	
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserFundingStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (UserFundingStatusEnum e : UserFundingStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
