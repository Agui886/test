package com.f.stock.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import annotation.NotNeedAuth;
import annotation.RequestLimit;
import cn.hutool.json.JSONUtil;
import constant.Constant;
import entity.common.Response;
import redis.RedisKeyPrefix;
import utils.RedisDao;
import vo.common.OnlineUserVO;
import vo.common.RequestLimitVO;
import vo.common.TokenUserVO;

@Configuration
public class MyMVCConfig implements WebMvcConfigurer {

	@Resource
	private RedisDao redisDao;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(handlerInterceptor)
				.addPathPatterns("/**")
				// 过滤路径
				.excludePathPatterns(excludePathPatterns());
	}

	public List<String> excludePathPatterns() {
		List<String> list = new ArrayList<>();
		list.add("/swagger-resources/**");
		list.add("/swagger-ui/**");
		list.add("/webjars/**");
		list.add("/error");
		list.add("/v3/api-docs");
		list.add("/test/**");
		list.add("/list/**");
		return list;
	}

	private boolean hasPermission(Object handler, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		String token = httpServletRequest.getHeader("Authorization");		
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			// 获取方法上的注解
			Method method = handlerMethod.getMethod();
			
			//请求限制
			RequestLimit requestLimit = method.getAnnotation(RequestLimit.class);		
			if(requestLimit != null) {
				long limitMillisecond = requestLimit.limitMillisecond();
				String servletPath = httpServletRequest.getServletPath();
				String methodName = method.getName();
				String key = RedisKeyPrefix.getServerRequestLimitKey(token == null ? httpServletRequest.getSession().getId() : token, servletPath, methodName);
				RequestLimitVO rl = redisDao.getBean(key, RequestLimitVO.class);
				if(rl == null) {
					rl = new RequestLimitVO();					
					rl.setServletPath(servletPath);
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
			String tokenKey = RedisKeyPrefix.getTokenUserKey(token);
			TokenUserVO tokenUser = redisDao.getBean(tokenKey, TokenUserVO.class);
			httpServletRequest.setAttribute(Constant.KEY_TOKEN_USER, tokenUser);
			NotNeedAuth notNeedAuth = method.getAnnotation(NotNeedAuth.class);
			// 验证权限
			if (notNeedAuth == null) {
				if (tokenUser == null) {
					httpServletResponse.setContentType("application/json; charset=utf-8");					
					PrintWriter writer = httpServletResponse.getWriter();
					//过期的TOKEN
					writer.write(JSONUtil.toJsonStr(Response.tokenExpire("您还未登录，请先登录")));
					return false;
				}
				String onlineKey = RedisKeyPrefix.getOnlineUserKey(tokenUser.getLoginId());
				OnlineUserVO onlineUser = redisDao.getBean(onlineKey, OnlineUserVO.class);
				if(onlineUser == null) {
					onlineUser = new OnlineUserVO();
					onlineUser.setLatestToken(token);
					onlineUser.setCurrentTimestamp(System.currentTimeMillis());	
					onlineUser.getTokenList().add(token);
					redisDao.setBean(onlineKey, onlineUser, 30, TimeUnit.MINUTES);
				} else {		
					redisDao.expire(onlineKey, 30, TimeUnit.MINUTES);
				}				
				redisDao.expire(tokenKey, 30, TimeUnit.MINUTES);				
			}						
		}
		return true;
	}

	HandlerInterceptor handlerInterceptor = new HandlerInterceptor() {
		@Override
		public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
				Object o) throws IOException {
			// 验证权限
			if (hasPermission(o, httpServletRequest, httpServletResponse)) {
				return true;
			}
			return false;
		}
	};
}
