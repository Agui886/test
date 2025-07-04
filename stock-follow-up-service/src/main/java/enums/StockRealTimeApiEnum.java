package enums;

/**
 * 股票实时行情API枚举类
 */
public enum StockRealTimeApiEnum {
	NOW_API("nowAPI", "nowAPI服务商"),
	QOS_API("qosAPI", "qosAPI服务商");
	
	private String code;
	
	private String name;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	StockRealTimeApiEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (StockRealTimeApiEnum e : StockRealTimeApiEnum.values()) {
			if (code == e.getCode()) {
				return e.getName();
			}
		}
		return null;
	}
	
	public static StockRealTimeApiEnum getByCode(String code) {
		for (StockRealTimeApiEnum stockRealTimeApiEnum : StockRealTimeApiEnum.values()) {
            if (stockRealTimeApiEnum.code.equals(code)) {
                return stockRealTimeApiEnum;
            }
        }
		return null;
	}
}
