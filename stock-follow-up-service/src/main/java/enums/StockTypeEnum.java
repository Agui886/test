package enums;

public enum StockTypeEnum {
	SH("sh", "沪股"),
	SZ("sz", "深股"),
	BJ("bj", "北证"),
	US("us", "美股"),
	HK("hk", "港股");
	
	private String code;
	
	private String name;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	StockTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (StockTypeEnum e : StockTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
	
	public static StockTypeEnum getByCode(String code) {
		for (StockTypeEnum stockTypeEnum : StockTypeEnum.values()) {
			if (stockTypeEnum.code.equals(code)) {
				return stockTypeEnum;
			}
		}
		return null;
	}
}
