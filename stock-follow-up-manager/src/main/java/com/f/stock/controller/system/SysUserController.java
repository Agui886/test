package com.f.stock.controller.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.f.stock.controller.BaseController;

import constant.SysConstant;
import entity.SysMenu;
import entity.SysUser;
import entity.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import service.SysMenuService;
import service.SysUserMenuRelationService;
import service.SysUserService;
import utils.GoogleAuthUtil;
import utils.PasswordGenerator;
import utils.StringUtil;
import vo.manager.SysUserEditVO;
import vo.manager.SysUserMenuSaveParamVO;
import vo.manager.SysUserMenuVO;
import vo.manager.SysUserSearchParamVO;

@RestController
@RequestMapping("/system/user")
@Api(tags = "系统设置")
@Slf4j
public class SysUserController extends BaseController {

	@Resource
	private SysUserService sysUserService;

	@Resource
	private SysMenuService sysMenuService;

	@Resource
	private SysUserMenuRelationService sysUserMenuRelationService;

	@Value("${spring.application.name}")
	private String appName;

	@ApiOperation("管理员列表")
	@PostMapping("/list")
	@ResponseBody
	public Response<Page<SysUser>> list(@RequestBody SysUserSearchParamVO param) {
		Page<SysUser> page = new Page<>(param.getPageNo(), param.getPageSize());
		LambdaQueryWrapper<SysUser> lqw = new LambdaQueryWrapper<>();
		if (param.getId() != null && param.getId() > 0) {
			lqw.eq(SysUser::getId, param.getId());
		}
		if (!StringUtil.isEmpty(param.getUsername())) {
			lqw.eq(SysUser::getUsername, param.getUsername());
		}
		if (!StringUtil.isEmpty(param.getRealName())) {
			lqw.eq(SysUser::getRealName, param.getRealName());
		}
		lqw.orderByDesc(SysUser::getId);
		sysUserService.page(page, lqw);
		return Response.successData(page);
	}

	@ApiOperation("管理员列表-添加")
	@PostMapping("/add")
	@ResponseBody
	public Response<Void> add(@RequestBody SysUserEditVO param) {
		if (StringUtil.isEmpty(param.getUsername())) {
			return Response.fail("请输入管理员账号");
		}
		if (StringUtil.isEmpty(param.getRealName())) {
			return Response.fail("请输入管理员名称");
		}
		if (StringUtil.isEmpty(param.getLoginPwd())) {
			return Response.fail("请输入登录密码");
		}
		if(sysUserService.lambdaQuery().eq(SysUser::getUsername, param.getUsername()).count() > 0) {
			return Response.fail("管理员[" + param.getUsername() + "]已存在，请重新输入");
		}
		SysUser s = new SysUser();
		s.setUsername(param.getUsername());
		s.setRealName(param.getRealName());
		s.setLoginPwd(PasswordGenerator.generate(SysConstant.PASSWORD_PREFIX, param.getLoginPwd()));
		s.setUserStatus(param.getUserStatus());
		s.setCreator(getUser().getOperator());
		s.insert();
		return Response.success();
	}

	@ApiOperation("管理员列表-修改")
	@PostMapping("/edit")
	@ResponseBody
	public Response<Void> edit(@RequestBody SysUserEditVO param) {
		if (StringUtil.isEmpty(param.getRealName())) {
			return Response.fail("请输入管理员名称");
		}
		SysUser old = this.sysUserService.getById(param.getId());
		if (old == null) {
			return Response.fail("管理员信息错误");
		}
		if (old.getId() == SysConstant.MANAGER_ACCOUNT_ID && getUser().getId() != SysConstant.MANAGER_ACCOUNT_ID) {
			return Response.fail("您没有修改超级管理员账号的权限！");
		}
		if (!StringUtil.isEmpty(param.getLoginPwd())) {
			old.setLoginPwd(PasswordGenerator.generate(SysConstant.PASSWORD_PREFIX, param.getLoginPwd()));
		}
		old.setRealName(param.getRealName());
		old.setUserStatus(param.getUserStatus());
		old.updateById();
		return Response.success();
	}

	@ApiOperation("管理员列表-删除")
	@PostMapping("/delete")
	@ResponseBody
	public Response<Void> delete(@ApiParam("管理员id") @RequestParam("id") Integer id) {
		if (id.equals(SysConstant.MANAGER_ACCOUNT_ID)) {
			return Response.fail("超级管理员账号无法删除");
		}
		this.sysUserService.removeById(id);
		return Response.success();
	}

	@ApiOperation("管理员列表-重置谷歌验证码")
	@PostMapping("/resetCode")
	@ResponseBody
	public Response<String> resetCode(@ApiParam("管理员id") @RequestParam("id") Integer id)
			throws UnsupportedEncodingException {
		SysUser old = this.sysUserService.getById(id);
		if (old == null) {
			return Response.fail("管理员信息错误");
		}
		if (old.getId() == SysConstant.MANAGER_ACCOUNT_ID) {
			if(getUser().getId() == SysConstant.MANAGER_ACCOUNT_ID) {
				return Response.fail("您无须设置验证码！");
			}
			return Response.fail("您没有修改超级管理员账号的权限！");
		}
		String s = GoogleAuthUtil.generaKey();
		String qrCodeData = GoogleAuthUtil.createGoogleAuthQRCodeData(s, old.getUsername(), appName);
		sysUserService.lambdaUpdate().eq(SysUser::getId, id).set(SysUser::getGoogleKey, s).set(SysUser::getGoogleAuthQrCodeData, qrCodeData).update();
		return Response.successData(GoogleAuthUtil.createGoogleAuthQRCodeData(s, old.getUsername(), appName));
	}

	@ApiOperation("管理员列表-权限配置")
	@PostMapping("/menuList")
	@ResponseBody
	public Response<List<SysUserMenuVO>> menuList(@ApiParam("管理员id") @RequestParam("id") Integer id) {
		SysUser currentUser = getUser();
		if (id == SysConstant.MANAGER_ACCOUNT_ID && currentUser.getId() != SysConstant.MANAGER_ACCOUNT_ID) {
			return Response.fail("您没有修改超级管理员账号的权限！");
		}
		if(sysUserService.lambdaQuery().eq(SysUser::getId, id).count() == 0) {
			return Response.fail("管理员账号不存在！");
		}
		List<SysUserMenuVO> smLst = new ArrayList<>();
		List<SysMenu> menusOfCurrentUser = sysMenuService.selectAllMenuIdByUserId(currentUser.getId(), null, false);
		if (id == SysConstant.MANAGER_ACCOUNT_ID) {
			menusOfCurrentUser.forEach(c -> {
				SysUserMenuVO sm = new SysUserMenuVO();
				sm.setSysUserId(id);
				sm.setTitle(c.getTitle());
				sm.setMenuId(c.getId());
				sm.setParentId(c.getParentId());
				sm.setMenuType(c.getMenuType());
				sm.setHasAuth(true);
				smLst.add(sm);
			});
		} else {
			List<SysMenu> menusOfSelectedUser = sysMenuService.selectAllMenuIdByUserId(id, null, false);
			menusOfCurrentUser.forEach(c -> {
				SysUserMenuVO sm = new SysUserMenuVO();
				sm.setSysUserId(id);
				sm.setTitle(c.getTitle());
				sm.setMenuId(c.getId());
				sm.setMenuType(c.getMenuType());
				sm.setParentId(c.getParentId());
				menusOfSelectedUser.forEach(s -> {
					if (c.getId().equals(s.getId())) {
						sm.setHasAuth(true);
					}
				});
				smLst.add(sm);
			});
		}
		List<SysUserMenuVO> collect = smLst.stream().filter(one -> one.getParentId() == 0).collect(Collectors.toList());
		findChildren(collect, smLst);
		return Response.successData(collect);
	}

	@ApiOperation("管理员列表-权限配置-保存")
	@PostMapping("/menuList/save")
	@ResponseBody
	public Response<Void> menuListSave(@RequestBody SysUserMenuSaveParamVO param) {
		return sysUserMenuRelationService.saveSysUserMenuRelation(param, getUser());
	}

	private void findChildren(List<SysUserMenuVO> c, List<SysUserMenuVO> p) {
		for (SysUserMenuVO s : c) {
			List<SysUserMenuVO> collect = p.stream().filter(one -> one.getParentId().equals(s.getMenuId()))
					.collect(Collectors.toList());
			s.setChildren(collect);
			findChildren(collect, p);
		}
	}

}
