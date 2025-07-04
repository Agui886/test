package utils.aliyun;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

public class AliyunFaceUtil {

	/**
	 * 解析-获取请求参数
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static TreeMap<String, String> getParams(HttpServletRequest request) throws UnsupportedEncodingException {
		 request.setCharacterEncoding("UTF-8");// 设置传输的编码
		 TreeMap<String, String> map = new TreeMap<String, String>();
		 Map reqMap = request.getParameterMap();
		 for (Object key : reqMap.keySet()) {
			 String value = ((String[]) reqMap.get(key))[0];
			 // System.out.println(key + ";" + value);
			 map.put(key.toString(), value);
		 }
		 return map;
	}
	
	/**
	 * 获取实人认证结果code消息
	 * @param code
	 * @return
	 */
	public static String getDescribeFaceVerifyCodeMessage(String code) {
		String message = "";
		switch (code) {
		case "200":
			message ="认证通过";
			break;
			
		case "201":
			message ="姓名和身份证不一致";
			break;
		
		case "202":
			message ="查询不到身份信息";
			break;
		
		case "203":
			message ="查询不到照片或照片不可用";
			break;
		
		case "204":
			message ="人脸比对不一致";
			break;
		
		case "205":
			message ="活体检测存在风险";
			break;
		
		case "206":
			message ="业务策略限制";
			break;
		
		case "207":
			message ="人脸与身份证人脸比对不一致";
			break;
		
		case "209":
			message ="权威比对源异常";
			break;
		
		case "220":
			message ="意愿判定不通过";
			break;
		
		case "221":
			message ="声纹比对不一致";
			break;
		
		case "222":
			message ="常规认证失败";
			break;
		
		default:
			message = "其他错误码："+code;
			break;
		}
		return message;
	}
}
