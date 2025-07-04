package com.f.stock.controller.system;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import entity.SiteAnnouncement;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SiteAnnouncementService;
import utils.StringUtil;

@RestController
@RequestMapping("/system/announcement")
@Api(tags = "系统设置")
public class AnnouncementController extends BaseController {

	@Resource
	private SiteAnnouncementService siteAnnouncementService;

	@ApiOperation("公告设置")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SiteAnnouncement>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
			@ApiParam("当前页码") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		Page<SiteAnnouncement> page = new Page<>(pageNo, pageSize);
		siteAnnouncementService.lambdaQuery().orderByAsc(SiteAnnouncement::getSort)
				.orderByDesc(SiteAnnouncement::getAddTime).page(page);
		return Response.successData(page);
	}

	@ApiOperation("公告设置-添加")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody SiteAnnouncement a) {
		if (StringUtil.isEmpty(a.getTitle())) {
			return Response.fail("请输入公告标题");
		}
		if (StringUtil.isEmpty(a.getContent())) {
			return Response.fail("请输入公告内容");
		}
		a.setId(null);
		a.setCreator(getUser().getOperator());
		a.insert();
		return Response.success();
	}

	@ApiOperation("公告设置-修改")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody SiteAnnouncement a) {
		if (StringUtil.isEmpty(a.getTitle())) {
			return Response.fail("请输入公告标题");
		}
		if (StringUtil.isEmpty(a.getContent())) {
			return Response.fail("请输入公告内容");
		}
		SiteAnnouncement old = this.siteAnnouncementService.getById(a.getId());
		if(old == null) {
			return Response.fail("公告信息错误");
		}
		old.setIsShow(a.getIsShow());
		old.setTitle(a.getTitle());
		old.setContent(a.getContent());
		old.setSort(a.getSort());
		old.setSource(a.getSource());
		old.updateById();
		return Response.success();
	}

	@ApiOperation("公告设置-修改")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("公告ID") @RequestParam(value = "id") Integer id) {
		this.siteAnnouncementService.removeById(id);
		return Response.success();
	}
}
