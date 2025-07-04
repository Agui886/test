package enums;

import java.math.BigDecimal;

public enum CurrencyEnum {
	CNY("CNY", "人民币", BigDecimal.ONE),
	USD("USD", "美元", new BigDecimal(0.1387)),
	HKD("HKD", "港元", new BigDecimal(1.0789));
	
	private String code;
	private String name;
	private BigDecimal defaultExchangeRate;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	public BigDecimal getDefaultExchangeRate() {
		return defaultExchangeRate;
	}

	CurrencyEnum(String code, String name, BigDecimal defaultExchangeRate) {
		this.code = code;
		this.name = name;
		this.defaultExchangeRate = defaultExchangeRate;
	}

	public static String getNameByCode(String code) {
		for (CurrencyEnum e : CurrencyEnum.values()) {
			if (code.equals(e.getCode())) {
				return e.getName();
			}
		}
		return null;
	}
	
	public static CurrencyEnum getByCode(String code) {
		for (CurrencyEnum e : CurrencyEnum.values()) {
			if (code.equals(e.getCode())) {
				return e;
			}
		}
		return null;
	}
	
	public static CurrencyEnum getByStockType(String type) {
		switch(type) {
		case "us":
			return CurrencyEnum.USD;
		case "hk":
			return CurrencyEnum.HKD;
		default:
			return CurrencyEnum.CNY;
		}
	}
}	
