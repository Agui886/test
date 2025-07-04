package enums;

public enum CashinTypeEnum {
	ManualRecharge("ManualRecharge", "人工充值"),
	Alipay("Alipay", "支付宝充值"),
	Bank("Bank", "银行卡充值");
	
	private String code;
	
	private String name;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	CashinTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (CashinTypeEnum e : CashinTypeEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
}
