package enums;

public enum TutorInfoChangeTypeEnum {
	
	CREATE("CREATE", "创建导师"),
	AVATAR("AVATAR", "修改头像"),
	NAME("NAME", "修改名称"),
	DOMAIN("DOMAIN", "修改公/私域"),
	FOLLOW_TYPE("FOLLOW_TYPE", "修改跟投类型"),
	PROFILE("PROFILE", "修改简介"),
	SORT("PROFILE", "修改排序"),
	EXPERIENCE("EXPERIENCE", "修改从业年限"),
	LABEL("LABEL", "修改标签"),
	CONFIGURATION("CONFIGURATION", "修改项目配置"),
	STATUS("STATUS", "修改状态"),
	DELETE("DELETE", "删除导师"),
	DO_MONTH_CONFIGURATION("DO_MONTH_CONFIGURATION", "上月项目数据统计");
	
	private String code;
	private String name;
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	TutorInfoChangeTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static String getNameByCode(String code) {
		for (TutorInfoChangeTypeEnum e : TutorInfoChangeTypeEnum.values()) {
			if (code.equals(e.code)) {
				return e.getName();
			}
		}
		return null;
	}
	
	public static TutorInfoChangeTypeEnum getByCode(String code) {
		for (TutorInfoChangeTypeEnum e : TutorInfoChangeTypeEnum.values()) {
			if (code.equals(e.code)) {
				return e;
			}
		}
		return null;
	}
}
