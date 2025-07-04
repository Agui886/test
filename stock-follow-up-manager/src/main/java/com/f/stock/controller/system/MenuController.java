package com.f.stock.controller.system;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.f.stock.controller.BaseController;

import entity.SysMenu;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import service.SysMenuService;

@RestController
@RequestMapping("/system/menu")
@Api(tags = "系统设置")
public class MenuController extends BaseController {
	
	@Resource
	private SysMenuService sysMenuService;
	
	@ApiOperation("菜单管理")
	@PostMapping("/list")
	@ResponseBody
	public Response<List<SysMenu>> list(@ApiParam("标题") @RequestParam(value = "title", required = false) String title) {
		return Response.successData(sysMenuService.selectAllMenuIdByUserId(getUser().getId(), title, true));
	}
	
	@ApiOperation(value ="菜单管理-添加", notes = "有无id区分新增还是编辑")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody SysMenu sysMenu) {
		return sysMenuService.add(sysMenu, getUser());
	}
	
	@ApiOperation(value = "菜单管理-编辑", notes = "有无id区分新增还是编辑")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody SysMenu sysMenu) {
		return sysMenuService.edit(sysMenu);
	}
	
	@ApiOperation("菜单管理-删除")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("菜单ID") @RequestParam("menuId") Integer menuId) {
		return sysMenuService.delete(menuId);
	}
}
