package entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel(description = "返回数据封装类")
public class Response<T> implements Serializable {
    public static final String SUCCESS = "200";

    public static final String FAIL = "400";//INVALID REQUEST

    public static final String FAIL_UNAUTHORIZED = "401";//Unauthorized
    
    public static final String FAIL_TOKEN = "402";//token失效

    public static final String FAIL_FORBIDDEN = "403";//Forbidden

    public static final String ERROR = "500";//INTERNAL SERVER ERROR


    @ApiModelProperty(name = "code", value = "状态")
    private String code = SUCCESS;

    @ApiModelProperty(name = "msg", value = "返回消息")
    private String msg = "success";

    @ApiModelProperty(name = "data", value = "返回消息实体")
    private T data;

    @ApiModelProperty(name = "info", value = "扩展字段")
    private Object info;

    public Response() {
    }

    private Response(T data) {
        this.data = data;
    }

    private Response(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    private Response(String msg, String code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public static Response<Void> success() {
        return new Response<>();
    }

    public static Response<String> success(String msg) {
        return new Response<String>(msg, SUCCESS);
    }

    public static <R> Response<R> successData(R data) {
        return new Response<>(data);
    }

    public static <R> Response<R> successData(R data, Object extend) {
        Response<R> response = new Response<>(data);
        response.setInfo(extend);
        return response;
    }

    public static <R> Response<R> successData(String msg, R data) {
        return new Response<>(msg, SUCCESS, data);
    }

    public static Response fail(String msg) {
        return new Response(msg, FAIL);
    }

    //无权限访问接口
    public static Response unauthorized(String msg){
        return new Response(msg, FAIL_UNAUTHORIZED);
    }

    //TOKEN失效,登录效验失败
    public static Response tokenExpire(String msg){
        return new Response(msg, FAIL_TOKEN);
    }

    //500异常同意返回
    public static Response error(String msg) {
        return new Response(msg, ERROR);
    }

    //500异常同意返回
    public static Response error(String msg, Object info) {
        Response response = new Response(msg, ERROR);
        response.setInfo(info);
        return response;
    }

    public static Response error(String code,String msg) {
        Response response = new Response(msg, code);
        return response;
    }

}
