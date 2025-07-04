package enums;

/**
 * 股票市场枚举类
 */
public enum StockMarketTypeEnum {
	Market_A(0, "A股-市场"),
	Market_Hk(1, "港股-市场"),
	Market_Us(2, "美股-市场");
	
	private Integer code;
	
	private String name;
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	StockMarketTypeEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(Integer code) {
		for (StockMarketTypeEnum e : StockMarketTypeEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
	
	public static StockMarketTypeEnum getByCode(Integer code) {
		for (StockMarketTypeEnum stockMarketTypeEnum : StockMarketTypeEnum.values()) {
            if (code != null && code == stockMarketTypeEnum.code) {
                return stockMarketTypeEnum;
            }
        }
		return null;
	}
}
