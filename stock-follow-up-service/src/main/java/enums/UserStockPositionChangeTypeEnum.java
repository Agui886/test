package enums;

public enum UserStockPositionChangeTypeEnum {
//	NEW_SHARE_TRANSFER_POSITION("NEW_SHARE_TRANSFER_POSITION", "新股转持仓"),
	POSITION_STATUS("POSITION_STATUS", "持仓状态变更"),
	LOCK_STATIS_EDIT("LOCK_PARAM_EDIT", "锁仓状态修改"),
	LOCK_IN_PERIOD("LOCK_IN_PERIOD", "锁仓天数修改"),
	CLOSING_POSITION("CLOSING_POSITION", "平仓");
	private final String code;
	private final String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserStockPositionChangeTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (UserStockPositionChangeTypeEnum e : UserStockPositionChangeTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
