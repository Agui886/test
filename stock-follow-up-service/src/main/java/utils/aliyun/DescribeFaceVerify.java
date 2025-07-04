package utils.aliyun;

import java.util.concurrent.CompletableFuture;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.cloudauth20190307.AsyncClient;
import com.aliyun.sdk.service.cloudauth20190307.models.DescribeFaceVerifyRequest;
import com.aliyun.sdk.service.cloudauth20190307.models.DescribeFaceVerifyResponse;

import cn.hutool.json.JSONUtil;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DescribeFaceVerify {
	public static void main(String[] args) throws Exception {
		String certifyId = "shaa8449ce5588f3a331ea97cbc7e20c";
		doDescribeFaceVerify(certifyId);
	}
    public static DescribeFaceVerifyResponse doDescribeFaceVerify(String certifyId) throws Exception {
    	log.info("====================金融级活体检测-获取认证详细数据-开始====================");
    	// 配置密钥信息
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId("LTAI5tDVy9HW3ekfu1TSnabH")
                .accessKeySecret("3kjdMXteGYBe8fQACL9NFSpTJDxYZX")
                .build());

        // 配置客户端链接
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration( ClientOverrideConfiguration.create().setEndpointOverride("cloudauth.aliyuncs.com"))
                .build();

        // 金融级活体检测-获取认证详细数据-请求参数
        DescribeFaceVerifyRequest describeFaceVerifyRequest = DescribeFaceVerifyRequest.builder()
        		.sceneId(1000012593L)//认证场景ID
        		.certifyId(certifyId)//实人认证唯一标识
                .build();
        log.info("金融级活体检测-获取认证详细数据-请求参数：{}",JSONUtil.toJsonStr(describeFaceVerifyRequest));
        
        // 获取-金融级活体检测-获取认证详细数据-返回结果
        CompletableFuture<DescribeFaceVerifyResponse> response = client.describeFaceVerify(describeFaceVerifyRequest);
        DescribeFaceVerifyResponse resp = response.get();
        log.info("金融级活体检测-获取认证详细数据-返回结果：{}",JSONUtil.toJsonStr(resp));

        // 关闭客户端
        client.close();
        return resp;
    }

}