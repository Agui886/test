package enums;

public enum StockPendingStatusEnum {
	//NONE(0, "待提交"),
	PENDING(1, "已委托"),
	COMPLETED(2, "已成交"),
	CANNELED(3, "已撤销"),
	REJECTED(4, "已拒绝"),
	EXPIRED(5, "已过期");
	private int code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	StockPendingStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(int code) {
		for (StockPendingStatusEnum e : StockPendingStatusEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
}
