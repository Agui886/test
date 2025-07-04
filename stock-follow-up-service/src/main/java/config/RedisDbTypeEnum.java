package config;

public enum RedisDbTypeEnum {
	DEFAULT(0, "默认数据缓存"),
	IP(1, "IP数据"),
	STOCK_A(2, "A股股票数据"),
	STOCK_HK(3, "港股股票数据"),
	STOCK_US(4, "美股股票数据"),
	STOCK_API(5, "股票外部API数据");
	
	RedisDbTypeEnum(Integer type, String name) {
		this.type = type;
		this.name = name;
	}
	
	private Integer type;
	
	private String name;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
