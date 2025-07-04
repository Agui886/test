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

import entity.SiteCarousel;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SiteCarouselService;
import utils.StringUtil;

@RestController
@RequestMapping("/system/carousel")
@Api(tags = "系统设置")
public class CarouselController extends BaseController {

	@Resource
	private SiteCarouselService siteCarouselService;

	@ApiOperation("轮播图设置")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SiteCarousel>> list(
			@ApiParam("当前页码") @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
			@ApiParam("当前页码") @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		Page<SiteCarousel> page = new Page<>(pageNo, pageSize);
		siteCarouselService.lambdaQuery().orderByAsc(SiteCarousel::getSort).orderByDesc(SiteCarousel::getAddTime).page(page);
		return Response.successData(page);
	}

	@ApiOperation("轮播图设置-添加")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody SiteCarousel a) {
		if (StringUtil.isEmpty(a.getImageData())) {
			return Response.fail("请选择轮播图");
		}
		a.setId(null);
		a.setCreator(getUser().getOperator());
		a.insert();
		return Response.success();
	}

	@ApiOperation("轮播图列表-修改")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody SiteCarousel a) {
		if (StringUtil.isEmpty(a.getImageData())) {
			return Response.fail("请选择轮播图");
		}
		SiteCarousel old = this.siteCarouselService.getById(a.getId());
		if(old == null) {
			return Response.fail("轮播图信息错误");
		}
		old.setIsShow(a.getIsShow());
		old.setSort(a.getSort());
		old.setImageData(a.getImageData());
		old.updateById();
		return Response.success();
	}

	@ApiOperation("轮播图列表-删除")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("轮播图ID") @RequestParam(value = "id") Integer id) {
		this.siteCarouselService.removeById(id);
		return Response.success();
	}
}
