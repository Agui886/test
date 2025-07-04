package service.impl;

import entity.SysMenu;
import entity.SysUser;
import entity.SysUserMenuRelation;
import entity.common.Response;
import mapper.SysMenuMapper;
import service.SysMenuService;
import service.SysUserMenuRelationService;
import utils.StringUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import constant.SysConstant;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 菜单信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
	@Resource
	private SysMenuMapper sysMenuMapper;
	
	@Resource
	private SysUserMenuRelationService sysUserMenuRelationService;

	@Override
	public Set<String> selectAllMenuAjaxByUserId(Integer userId) {
		return sysMenuMapper.selectAllMenuAjaxByUserId(userId);
	}

	@Override
	public List<SysMenu> selectAllMenuIdByUserId(Integer userId, String title, boolean setChildren) {
		List<SysMenu> list;
        if(userId.equals(SysConstant.MANAGER_ACCOUNT_ID)) {
        	LambdaQueryWrapper<SysMenu> lqw = new LambdaQueryWrapper<>();
        	lqw.orderByAsc(SysMenu::getSort);
        	if(!StringUtil.isEmpty(title)) {
        		lqw.eq(SysMenu::getTitle, title);
        	}
        	list = this.list(lqw);
        } else {
        	list = sysMenuMapper.selectAllMenuIdByUserId(userId, title);
        }
        if(setChildren) {
			List<SysMenu> collect = list.stream().filter(one -> one.getParentId() == 0).collect(Collectors.toList());
			findChildren(collect, list);
			return collect;
        }
        return list;
	}

	@Override
	@Transactional
	public Response<Void> add(SysMenu sysMenu, SysUser sysUser) {
		if(StringUtil.isEmpty(sysMenu.getTitle())) {
			return Response.fail("请输入菜单标题");
		}
		if(StringUtil.isEmpty(sysMenu.getPath())) {
			return Response.fail("请输入前端URL");
		}
//		if(StringUtil.isEmpty(sysMenu.getRouting())) {
//			return Response.fail("请输入前端路由地址");
//		}
		sysMenu.setId(null);
		sysMenu.insert();
		SysUserMenuRelation smr = new SysUserMenuRelation();
		smr.setSysUserId(sysUser.getId());
		smr.setSysMenuId(sysMenu.getId());
		smr.setCreator(sysUser.getOperator());
		smr.insert();
		return Response.success();
	}
	
	@Override
	public Response<Void> edit(SysMenu sysMenu) {
		if(sysMenu.getId() == null || sysMenu.getId() == 0) {
			return Response.fail("菜单信息错误");
		}
		if(StringUtil.isEmpty(sysMenu.getTitle())) {
			return Response.fail("请输入菜单标题");
		}
//		this.lambdaUpdate().eq(SysMenu::getId, sysMenu.getId())
//			.set(SysMenu::getParentId, sysMenu.getParentId())
//			.set(SysMenu::getSort, sysMenu.getSort())
//			.set(SysMenu::getAjax, sysMenu.getAjax())
//			.set(SysMenu::getIcon, sysMenu.getIcon())
//			.set(SysMenu::getMenuType, sysMenu.getMenuType())
//			.set(SysMenu::getTitle, sysMenu.getTitle())
//			.update();
		sysMenu.updateById();
		return Response.success();
	}

	@Override
	@Transactional
	public Response<Void> delete(Integer menuId) {
		if(this.lambdaQuery().eq(SysMenu::getId, menuId).count() == 0) {
			return Response.fail("菜单信息错误");
		}
		// 判断子集
        List<SysMenu> list = this.lambdaQuery().eq(SysMenu::getParentId, menuId).list();
        if (list.size() > 0) {
            return Response.fail("请先删除下级当前菜单的下级菜单");
        }
		this.lambdaUpdate().eq(SysMenu::getId, menuId).remove();
		sysUserMenuRelationService.lambdaUpdate().eq(SysUserMenuRelation::getSysMenuId, menuId).remove();
		return Response.success();
	}
	
	private void findChildren(List<SysMenu> list, List<SysMenu> sysMenus) {
		for (SysMenu sysMenu : list) {
			List<SysMenu> collect = sysMenus.stream().filter(one -> one.getParentId().equals(sysMenu.getId())).collect(Collectors.toList());
			sysMenu.setChildren(collect);
			findChildren(collect, sysMenus);
		}
	}
}
