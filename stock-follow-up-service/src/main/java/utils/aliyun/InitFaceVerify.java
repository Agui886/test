package utils.aliyun;


import java.util.concurrent.CompletableFuture;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.cloudauth20190307.AsyncClient;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyRequest;
import com.aliyun.sdk.service.cloudauth20190307.models.InitFaceVerifyResponse;

import cn.hutool.json.JSONUtil;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import utils.StringUtil;

@Slf4j
public class InitFaceVerify {
	public static void main(String[] args) throws Exception {
		String userId = "1";
		String certName = "张三";
		String certNo ="4444";
		String returnUrl = "https://www.baidu.com";
		String metaInfo = "{\"bioMetaInfo\":\"4.1.0:2916352,0\",\"deviceType\":\"h5\",\"ua\":\"Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1\",\"guardToken\":\"\"}";		
		doInitFaceVerify(getInitFaceVerifyRequestIdpro(certName,certNo,returnUrl,metaInfo));
	}
	
	/**
	 * 金融级活体检测-发起认证请求
	 * @param certName 您的终端用户的真实姓名。
	 * @param certNo 您的终端用户的证件号码。
	 * @param returnUrl 认证结束后回跳页面的链接地址。
	 * @param metaInfo MetaInfo环境参数。实际环境需要通过JS文件，调用函数getMetaInfo()获取
	 * @return
	 * @throws Exception
	 */
    public static InitFaceVerifyResponse doInitFaceVerify(InitFaceVerifyRequest initFaceVerifyRequest) throws Exception {
    	log.info("====================金融级活体检测-发起认证请求-开始====================");
        // 配置密钥信息
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId("LTAI5tDVy9HW3ekfu1TSnabH")
                .accessKeySecret("3kjdMXteGYBe8fQACL9NFSpTJDxYZX")
                .build());

        // 配置客户端链接
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(ClientOverrideConfiguration.create().setEndpointOverride("cloudauth.aliyuncs.com"))
                .build();
        
        //金融级活体检测-请求参数
        log.info("金融级活体检测-发起认证请求-请求参数：{}",JSONUtil.toJsonStr(initFaceVerifyRequest));
        
        //获取-金融级活体检测-请求的返回结果
        CompletableFuture<InitFaceVerifyResponse> response = client.initFaceVerify(initFaceVerifyRequest);
        InitFaceVerifyResponse resp = response.get();
        log.info("金融级活体检测-发起认证请求-返回结果：{}",JSONUtil.toJsonStr(resp));
        
        // 关闭客户端
        client.close();
        log.info("====================金融级活体检测-发起认证请求-结束====================");
        return resp;
    }
    
    /**
     * 金融级人脸验证方案(【身份证、姓名】检测认证)-请求参数
     * @param userId
     * @param returnUrl
     * @param metaInfo
     * @return
     */
    public static InitFaceVerifyRequest getInitFaceVerifyRequestIdpro(String certName,String certNo,String returnUrl,String metaInfo) {
   	 		InitFaceVerifyRequest initFaceVerifyRequest = InitFaceVerifyRequest.builder()
        		.sceneId(1000012593L)//认证场景ID。您可以在控制台创建认证场景后获得的场景ID，具体操作，请参见添加认证场景。
                .outerOrderNo(StringUtil.uuid32())//客户服务端自定义的业务唯一标识，用于后续定位排查问题使用。值最长为32位长度的字母数字组合，请确保唯一。                
                .productCode("ID_PRO")//认证方案。唯一取值：ID_PRO。
                .model("MULTI_ACTION")//要进行活体检测的类型。[LIVENESS（默认）：眨眼动作活体检测。PHOTINUS_LIVENESS：眨眼动作活体+炫彩活体双重检测。MULTI_ACTION：多动作活体检测，眨眼+任意摇头检测（顺序随机）。MOVE_ACTION（推荐）：远近动作+眨眼动作活体检测。MULTI_FAPTCHA：多动作活体检测，眨眼+形迹判断（顺序随机）。]               
                .certType("IDENTITY_CARD")//用户证件类型。不同证件类型，取值均为IDENTITY_CARD。
                .certName(certName)//您的终端用户的真实姓名。
                .certNo(certNo)//您的终端用户的证件号码。
                .returnUrl(returnUrl)//客户业务页面回跳的目标地址。
                .metaInfo(metaInfo)//MetaInfo环境参数。实际环境需要通过JS文件，调用函数getMetaInfo()获取，详情请参见H5网页集成。
                //.callbackUrl("https://sn1uy503814.vicp.fun/user/aliyunInitFaceVerifyCallback")//回调地址
                //.userId(userId.toString())//客户业务自定义的用户ID，请保持唯一。
                .build();
   	 return initFaceVerifyRequest;
   }
    
    /**
     * 金融级人脸验证方案(【照片】检测认证)-请求参数
     * @param userId
     * @param faceContrastPicture
     * @param returnUrl
     * @param metaInfo
     * @return
     */
    public static InitFaceVerifyRequest getInitFaceVerifyRequestPvfv(String userId, String faceContrastPicture,String returnUrl,String metaInfo) {
      	 	InitFaceVerifyRequest initFaceVerifyRequest = InitFaceVerifyRequest.builder()
      	 	   .sceneId(1000012593L)//认证场景ID。您可以在控制台创建认证场景后获得的场景ID，具体操作，请参见添加认证场景。
			   .outerOrderNo(StringUtil.uuid32())//客户服务端自定义的业务唯一标识，用于后续定位排查问题使用。值最长为32位长度的字母数字组合，请确保唯一。                
			   .productCode("PV_FV")//认证方案。唯一取值：PV_FV。
			   .model("MULTI_ACTION")//要进行活体检测的类型。[LIVENESS（默认）：眨眼动作活体检测。PHOTINUS_LIVENESS：眨眼动作活体+炫彩活体双重检测。MULTI_ACTION：多动作活体检测，眨眼+任意摇头检测（顺序随机）。MOVE_ACTION（推荐）：远近动作+眨眼动作活体检测。MULTI_FAPTCHA：多动作活体检测，眨眼+形迹判断（顺序随机）。]               
			   .certType("IDENTITY_CARD")//用户证件类型。不同证件类型，取值均为IDENTITY_CARD。
			   .returnUrl(returnUrl)//客户业务页面回跳的目标地址。
			   .metaInfo(metaInfo)//MetaInfo环境参数。实际环境需要通过JS文件，调用函数getMetaInfo()获取，详情请参见H5网页集成。
			   .userId(userId.toString())//客户业务自定义的用户ID，请保持唯一。
			   .faceContrastPicture(faceContrastPicture)//照片Base64编码。如果您选择FaceContrastPicture（照片Base64编码）方式传入人脸照片，请注意检查照片大小，不要传入过大的照片；您可以通过OSS方式上传较大的人脸照片。
			   //.faceContrastPictureUrl(faceContrastPictureUrl)//人像地址，公网可访问的HTTP、HTTPS链接。
			   //.certifyId(certifyId)//之前实人认证通过的CertifyId，认证时的照片作为比对照片。您需要确保传入CertifyId关联的图片正常存储在授权的OSS Bucket中。
			   .build();
      	 return initFaceVerifyRequest;
      }
    
    
    

}