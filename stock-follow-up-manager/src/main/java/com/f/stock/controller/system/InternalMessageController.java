package com.f.stock.controller.system;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.SiteInternalMessage;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import service.SiteInternalMessageService;
import utils.StringUtil;
import vo.manager.SiteInternalMessageSearchParamVO;

@RestController
@RequestMapping("/system/internalMessage")
@Api(tags = "系统设置")
public class InternalMessageController {
	
	@Resource
	private SiteInternalMessageService siteInternalMessageService;
	
	@ApiOperation("站内消息")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SiteInternalMessage>> list(SiteInternalMessageSearchParamVO param) {
		Page<SiteInternalMessage> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<SiteInternalMessage> lqw = new LambdaQueryWrapper<>();
		if(param.getUserId() != null && param.getUserId() > 0) {
			lqw.eq(SiteInternalMessage::getUserId, param.getUserId());
		}
		if(!StringUtil.isEmpty(param.getTitle())) {
			lqw.like(SiteInternalMessage::getTitle, param.getTitle());
		}
		if(param.getStartTime() != null) {
			lqw.ge(SiteInternalMessage::getSendTime, param.getStartTime());
		}
		if(param.getEndTime() != null) {
			lqw.le(SiteInternalMessage::getSendTime, param.getEndTime());
		}
		lqw.orderByDesc(SiteInternalMessage::getId);
		siteInternalMessageService.page(page, lqw);
		return Response.successData(page);
	}
}
