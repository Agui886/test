package com.f.stock.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import constant.Constant;
import utils.IPUtil;
import vo.common.TokenUserVO;

public class BaseController {

    @Resource
    protected HttpServletRequest httpServletRequest;
    
    public String getIp(){
        return IPUtil.getIpAddr(httpServletRequest);
    }

    protected TokenUserVO getTokenUser(){    	
    	Object attr = httpServletRequest.getAttribute(Constant.KEY_TOKEN_USER);
    	if(attr == null) {
    		return null;
    	}
        return (TokenUserVO) attr;
    }


}
