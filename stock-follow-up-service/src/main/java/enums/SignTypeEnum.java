package enums;

public enum SignTypeEnum {
	BUY(0, "买入"),
	SALE(1, "卖出");
	
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	SignTypeEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (SignTypeEnum e : SignTypeEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
