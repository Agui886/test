package utils;

public class XORUtil {
	private static int ENCRYPT_KEY_LEN = 8;

	/**
	 * 数据解密
	 * 
	 * @param encrypData
	 * @return
	 */
	public static String XorCrevasse(String encrypData) {
		StringBuilder builder = new StringBuilder();
		int length = encrypData.length();
		if (length < (ENCRYPT_KEY_LEN * 8)) {
			return "";
		}
		int num2 = Integer.parseInt(encrypData.substring(0, 4), 0x10);
		if (length != (((((num2 + ENCRYPT_KEY_LEN) - 1) / ENCRYPT_KEY_LEN) * ENCRYPT_KEY_LEN) * 8)) {
			return "";
		}

		for (int i = 0; i < num2; i++) {
			String str2 = "";
			String str = "";
			str2 = substring(i * 8, 4, encrypData.getBytes());
			str = substring((i * 8) + 4, 4, encrypData.getBytes());
			int num4 = Integer.parseInt(str2, 0x10);
			int num5 = Integer.parseInt(str, 0x10);
			builder.append((char) (num4 ^ num5));
		}
		return builder.toString();
	}

	/**
	 * 数据加密
	 * 
	 * @param sourceData
	 * @return
	 */
	public static String XorEncrypt(String sourceData) {
		StringBuilder builder = new StringBuilder();
		int[] numArray = new int[ENCRYPT_KEY_LEN];
		numArray[0] = sourceData.length();
		for (int i = 1; i < numArray.length; i++) {
			numArray[i] = (int) ((Math.random() * 0xffff) % 0xffff);
		}
		int num = 0;
		int num2 = (int) ((((numArray[0] + ENCRYPT_KEY_LEN) - 1) / ENCRYPT_KEY_LEN) * ENCRYPT_KEY_LEN);
		String[] sourceArr = sourceData.split("");
		for (int num3 = 0; num3 < num2; num3 = num3 + 1) {
// num = ((num3 >= array[0]) ? 
	//((ushort)(array[(int)num3 % (int)ENCRYPT_KEY_LEN] ^ (ushort)(random.Next(0, 65535) % 65535))) : 
	//((ushort)(sourceData[num3] ^ array[(int)num3 % (int)ENCRYPT_KEY_LEN])));
			if (num3 >= numArray[0]) {
				num = (int) (numArray[num3 % ENCRYPT_KEY_LEN] ^ ((int) ((Math.random() * 0xffff) % 0xffff)));
			} else {
				num = (sourceArr[num3].hashCode() ^ numArray[num3 % ENCRYPT_KEY_LEN]);
			}
			builder.append(stringToHex(numArray[num3 % ENCRYPT_KEY_LEN])).append(stringToHex(num));
		}
		return builder.toString();
	}

	/**
	 * 十六进制转换缺位补0
	 * 
	 * @param value
	 * @return
	 */
	private static String stringToHex(int value) {
		String toHex = Integer.toHexString(value).toUpperCase();
		switch (toHex.length()) {
		case 1:
			toHex = "000" + toHex;
			break;
		case 2:
			toHex = "00" + toHex;
			break;
		case 3:
			toHex = "0" + toHex;
			break;
		default:
			break;
		}
		return toHex;
	}	

	/**
	 * 字符串截取
	 * 
	 * @param startIndex 起始位置
	 * @param length     截取字符串长度
	 * @param byt        字符字节
	 * @return
	 */
	private static String substring(int startIndex, int length, byte[] byt) {
		return new String(byt, startIndex, length);
	}
	
    public static void main(String[] args) {               
        String encrypData = XorEncrypt("lzkj");
        System.out.println(encrypData);

//		boolean flag = checkBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5Ojf");
		boolean b = checkBase64("");
		System.out.println(b);
    }

	public static boolean checkBase64(String base64) {
		if (base64 == null || base64.equals("")) {
			return true;
		}

		if (base64.startsWith("data:image")) {
			return true;
		}else {
			return false;
		}
	}
}