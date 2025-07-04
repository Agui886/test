package config;


import entity.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // 系统异常
    @ExceptionHandler(value = Exception.class)
    public Response<Void> exceptionHandler(Exception exception, HttpServletRequest httpServletRequest) {
        log.error("系统异常:{}",httpServletRequest.getPathInfo(), exception);
        return Response.fail("系统异常,请稍后再试");
    }

    // 业务异常
    @ExceptionHandler(value = ServiceException.class)
    public Response<Void> exceptionHandler(ServiceException exception, HttpServletRequest httpServletRequest) {
        log.error("业务异常:{}",httpServletRequest.getPathInfo(), exception);
        return Response.fail(exception.getMessage());
    }
}
