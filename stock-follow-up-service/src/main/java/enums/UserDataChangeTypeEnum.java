package enums;

public enum UserDataChangeTypeEnum {
	ADD_USER("ADD_USER", "新增会员"), 
	EDIT_USER("EDIT_USER", "编辑会员"), 
	USER_REGISTER("USER_REGISTER", "会员注册"), 
	BANK("BANK", "修改银行卡"),
	REAL_AUTH_STATUS("REAL_AUTH_STATUS", "开户审核"),
	FUNDING_STATUS("FUNDING_STATUS", "融资审核")
	;

	private final String code;
	private final String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	UserDataChangeTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (UserDataChangeTypeEnum e : UserDataChangeTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
