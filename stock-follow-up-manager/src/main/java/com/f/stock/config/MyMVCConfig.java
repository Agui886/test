package com.f.stock.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import annotation.NotNeedAuth;
import annotation.RequestLimit;
import cn.hutool.json.JSONUtil;
import constant.SysConstant;
import entity.SysUser;
import entity.common.Response;
import redis.RedisKeyPrefix;
import service.SysIpWhitelistService;
import service.SysMenuService;
import service.SysUserService;
import utils.RedisDao;
import vo.common.OnlineUserVO;
import vo.common.RequestLimitVO;
import vo.common.SysTokenVO;

@Configuration
public class MyMVCConfig implements WebMvcConfigurer {


    @Resource
    private RedisDao redisDao;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysUserService sysUserService;
    
    @Resource
    private SysIpWhitelistService sysIpWhitelistService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor)
                //过滤路径
                .excludePathPatterns(excludePathPatterns())
                .addPathPatterns("/**");
    }

    public List<String> excludePathPatterns() {
        List<String> list = new ArrayList<>();
        list.add("/swagger-resources/**");
        list.add("/swagger-ui/**");
        list.add("/webjars/**");
        list.add("/error");
        list.add("/v3/api-docs");
        return list;
    }

    private boolean hasPermission(Object handler, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        if (handler instanceof HandlerMethod) {
//        	String ip = IPUtil.getIpAddr(httpServletRequest);
//        	if(!ip.equals("0:0:0:0:0:0:0:1") && !ip.equals("127.0.0.1") && !ip.equals("localhost")) {
//        		SysIpWhitelist si= sysIpWhitelistService.lambdaQuery().eq(SysIpWhitelist::getIpv4Val, ip).last("limit 1").one();
//            	if(si == null) {
//            		 httpServletResponse.setContentType("application/json; charset=utf-8");
//                     PrintWriter writer = httpServletResponse.getWriter();
//                     writer.write(JSONUtil.toJsonStr(Response.unauthorized("访问受限，IP: " + ip + " 未授权")));
//                     return false;
//            	}
//        	}
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String token = httpServletRequest.getHeader("Authorization");
            //请求限制
            RequestLimit requestLimit = method.getAnnotation(RequestLimit.class);
            if(requestLimit != null) {
                long limitMillisecond = requestLimit.limitMillisecond();
                String methodName = method.getName();
                String key = RedisKeyPrefix.getServerRequestLimitKey(token == null ? httpServletRequest.getSession().getId() : token, httpServletRequest.getServletPath(), methodName);
                RequestLimitVO rl = redisDao.getBean(key, RequestLimitVO.class);
                if(rl == null) {
                    rl = new RequestLimitVO();
                    rl.setServletPath(httpServletRequest.getServletPath());
                    rl.setMethodName(methodName);
                    redisDao.setBean(key, rl, limitMillisecond, TimeUnit.MILLISECONDS);
                } else {
                    if(rl.getLastTimeMillis() + limitMillisecond > System.currentTimeMillis()) {
                        httpServletResponse.setContentType("application/json; charset=utf-8");
                        PrintWriter writer = httpServletResponse.getWriter();
                        //操作太频繁
                        writer.write(JSONUtil.toJsonStr(Response.fail(requestLimit.errMsg())));
                        return false;
                    }
                }
            }
            
            //获取方法上的注解
            NotNeedAuth notNeedAuth = handlerMethod.getMethod().getAnnotation(NotNeedAuth.class);
            // 验证权限
            if (notNeedAuth == null) {
            	// 判断有没有token
                if (StringUtils.isBlank(token)) {
                    httpServletResponse.setContentType("application/json; charset=utf-8");
                    PrintWriter writer = httpServletResponse.getWriter();
                    //过期的TOKEN
                    writer.write(JSONUtil.toJsonStr(Response.tokenExpire("您的会话已超时，请重新登录")));
                    return false;
                }
                String tokenKey = RedisKeyPrefix.getSysTokenKey(token);
                SysTokenVO st  = redisDao.getBean(tokenKey, SysTokenVO.class);
                if (st ==  null) {
                    httpServletResponse.setContentType("application/json; charset=utf-8");
                    PrintWriter writer = httpServletResponse.getWriter();
                    //过期的TOKEN
                    writer.write(JSONUtil.toJsonStr(Response.tokenExpire("您的会话已超时，请重新登录")));
                    return false;
                }
                SysUser sysUser = st.getSysUser();
                httpServletRequest.setAttribute("AuthUser", sysUser);
                String onlineSysUserKey = RedisKeyPrefix.getOnlineSysUserKey(sysUser.getId());
                OnlineUserVO ou = redisDao.getBean(onlineSysUserKey, OnlineUserVO.class);
                if(ou == null) {
                	ou = new OnlineUserVO();
                	ou.setCurrentTimestamp(System.currentTimeMillis());
                	ou.setLatestToken(token);
                	redisDao.setBean(onlineSysUserKey, ou, 1, TimeUnit.DAYS);
                	redisDao.expire(tokenKey, 1, TimeUnit.DAYS);
                } else {
                	if(!ou.getLatestToken().equals(token)){
                        httpServletResponse.setContentType("application/json; charset=utf-8");
                        PrintWriter writer = httpServletResponse.getWriter();
                        //异地被登录
                        writer.write(JSONUtil.toJsonStr(Response.tokenExpire("您的会话已超时，请重新登录")));
                        return false;
                    }
                	redisDao.expire(onlineSysUserKey, 1, TimeUnit.DAYS);
                	redisDao.expire(tokenKey, 1, TimeUnit.DAYS);
                }
                String requestUri = httpServletRequest.getRequestURI();
                if(requestUri.startsWith("/getMyMenus") 
                		|| requestUri.startsWith("/personal/setting")
                		|| requestUri.startsWith("/list")
                		|| requestUri.startsWith("/logout")
                		|| requestUri.startsWith("/myInformation")
                		|| sysUser.getUsername().equals(SysConstant.MANAGER_ACCOUNT_NAME)) {
                	return true;
                }
                Set<String> methods = sysMenuService.selectAllMenuAjaxByUserId(sysUser.getId());
                if (CollectionUtils.isEmpty(methods)) {
                    httpServletResponse.setContentType("application/json; charset=utf-8");
                    PrintWriter writer = httpServletResponse.getWriter();
                    writer.write(JSONUtil.toJsonStr(Response.unauthorized("没有权限")));
                    return false;
                }
                for (String string : methods) {
                    if (httpServletRequest.getRequestURI().startsWith(string)) {
                        return true;
                    }
                }
                httpServletResponse.setContentType("application/json; charset=utf-8");
                PrintWriter writer = httpServletResponse.getWriter();
                writer.write(JSONUtil.toJsonStr(Response.unauthorized("没有权限")));
                return false;
            }
        }
        return true;
    }
    
    HandlerInterceptor handlerInterceptor = new HandlerInterceptor() {
        @Override
        public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws IOException {
            // 验证权限
            return hasPermission(o, httpServletRequest, httpServletResponse);
        }
    };
}
