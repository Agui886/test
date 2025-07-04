package utils;

import java.util.UUID;

import constant.Constant;
import constant.SysConstant;

public class PasswordGenerator {
	
	public static String createToken(Integer id, Long currentTimestamp) {
		return MD5Util.MD5Encode(id + "^O^" + currentTimestamp, "");
	}
	
	public static String createSysUserToken(Integer id) {
		return MD5Util.MD5Encode(id + "^oo^" + UUID.randomUUID(), "");
	}
	
	public static String generate(final String PREFIX, String password) {
		return MD5Util.MD5Encode(PREFIX + "@@" + password, "");
	}
	
	public static void main(String[] args) {
		System.err.println(PasswordGenerator.generate(Constant.PASSWORD_PREFIX, "123456"));	
		System.err.println(PasswordGenerator.generate(SysConstant.PASSWORD_PREFIX, "123456"));	
	}
}
