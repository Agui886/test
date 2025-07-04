package com.f.stock.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import entity.SysUser;
import utils.IPUtil;

public class BaseController {

    @Resource
    protected HttpServletRequest httpServletRequest;

    public SysUser getUser(){
       return (SysUser) httpServletRequest.getAttribute("AuthUser");
    }

    public String getIp(){
        return IPUtil.getIpAddr(httpServletRequest);
    }
    
    public String getToken() {
    	return httpServletRequest.getHeader("Authorization");
    }
}
