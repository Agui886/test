package com.f.stock.controller.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.SiteInternalMessage;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SiteInternalMessageService;
import vo.common.TokenUserVO;

@RestController
@RequestMapping("/user/internalMessage")
@Api(tags = "我的")
public class InternalMessageController extends BaseController {
	
	@Resource
	private SiteInternalMessageService siteInternalMessageService;
	
	@ApiOperation("站内消息")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SiteInternalMessage>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@ApiParam("每页条数") @RequestParam(value = "pageSize", defaultValue = "50", required = false) Integer pageSize
			) {
		if(pageSize > 50) {
			pageSize = 50;
		}
		Page<SiteInternalMessage> page = new Page<>(pageNo, pageSize);
		LambdaQueryWrapper<SiteInternalMessage> lqw = new LambdaQueryWrapper<>();
		lqw.select(SiteInternalMessage::getId, SiteInternalMessage::getTitle, SiteInternalMessage::getContent, SiteInternalMessage::getIsRead, SiteInternalMessage::getSendTime, SiteInternalMessage::getCreator);
		lqw.eq(SiteInternalMessage::getUserId, super.getTokenUser().getLoginId());
		lqw.orderByDesc(SiteInternalMessage::getId);
		siteInternalMessageService.page(page, lqw);
		List<SiteInternalMessage> list = page.getRecords();
		if(list.size() > 0) {
			List<Integer> ids = new ArrayList<>();
			list.forEach(i->{
				ids.add(i.getId());
			});
			siteInternalMessageService.lambdaUpdate().set(SiteInternalMessage::getIsRead, true).in(SiteInternalMessage::getId, ids).update();
		}
		return Response.successData(page);
	}
	
	@ApiOperation("站内消息-删除，可批量删除")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("站内信id集合") @RequestParam(value = "id") List<Integer> ids) {
		TokenUserVO tu = super.getTokenUser();
		if(ids.size() == 0) {
			return Response.fail("请选择消息");
		}
		siteInternalMessageService.lambdaUpdate().eq(SiteInternalMessage::getUserId, tu.getLoginId()).in(SiteInternalMessage::getId, ids).remove();
		return Response.success();
	}
}
