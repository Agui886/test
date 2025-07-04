package enums;

public enum CashoutTypeEnum {
	ManualRecharge("ManualRecharge", "人工提现"),
	Alipay("Alipay", "支付宝提现"),
	Bank("Bank", "银行卡提现");
	
	private String code;
	
	private String name;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	CashoutTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (CashoutTypeEnum e : CashoutTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
