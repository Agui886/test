package service.impl;

import entity.SysMenu;
import entity.SysUser;
import entity.SysUserMenuRelation;
import entity.common.Response;
import mapper.SysUserMenuRelationMapper;
import service.SysMenuService;
import service.SysUserMenuRelationService;
import service.SysUserService;
import vo.manager.SysUserMenuSaveParamVO;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import constant.SysConstant;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台账号权限配置表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
@Service
public class SysUserMenuRelationServiceImpl extends ServiceImpl<SysUserMenuRelationMapper, SysUserMenuRelation> implements SysUserMenuRelationService {

	@Resource
	private SysMenuService sysMenuService;
	
	@Resource
	private SysUserService sysUserService;
	
	@Override
	public Response<Void> saveSysUserMenuRelation(SysUserMenuSaveParamVO param, SysUser operator) {
		if(param.getSysUserId() == null) {
			return Response.fail("请选择要操作的管理员");
		}
		if(param.getSysUserId() == SysConstant.MANAGER_ACCOUNT_ID) {
			return Response.fail("超级管理员的权限不允许被修改！");
		}
		if(sysUserService.lambdaQuery().eq(SysUser::getId, param.getSysUserId()).count() == 0) {
			return Response.fail("管理员账号不存在！");
		}
		List<Integer> selectedIds = param.getMenuIdList();
		List<SysMenu> authListOfOperator = sysMenuService.selectAllMenuIdByUserId(operator.getId(), null, false);
		if(selectedIds.size() == 0) {
			List<Integer> deleteIds = new ArrayList<>();
			for(SysMenu auth : authListOfOperator) {
				deleteIds.add(auth.getId());
			}
			this.lambdaUpdate().eq(SysUserMenuRelation::getSysUserId, param.getSysUserId()).in(SysUserMenuRelation::getSysMenuId, deleteIds).remove();
		} else {
			List<Integer> addIds = new ArrayList<>();
			List<Integer> deleteIds = new ArrayList<>();
			List<SysMenu> authListOfSelectedUser = sysMenuService.selectAllMenuIdByUserId(param.getSysUserId(), null, false);
	outLoop:for(Integer selectedId : selectedIds) {
				for(SysMenu auth : authListOfSelectedUser) {
					if(selectedId.equals(auth.getId())) {
						continue outLoop;
					}
				}
				addIds.add(selectedId);
			}
	outLoop:for(SysMenu auth : authListOfSelectedUser) {
				for(Integer selectedId : selectedIds) {
					if(selectedId.equals(auth.getId())) {
						continue outLoop;
					}
				}
				deleteIds.add(auth.getId());
			}
			if(addIds.size() > 0) {
				List<SysUserMenuRelation> smrLst = new ArrayList<>();
				for(Integer addId : addIds) {
					boolean isExists = false;
					for(SysMenu auth : authListOfOperator) {
						if(addId.equals(auth.getId())) {
							isExists = true;
							break;
						}
					}
					if(!isExists) {
						return Response.fail("您没有添加菜单【id:" + addId + "】的权限");
					}
					SysUserMenuRelation smr = new SysUserMenuRelation();
					smr.setSysMenuId(addId);
					smr.setSysUserId(param.getSysUserId());
					smr.setCreator(operator.getOperator());
					smrLst.add(smr);
				}
				if(smrLst.size() > 0) {
					this.saveBatch(smrLst);
				}
			}
			if(deleteIds.size() > 0) {
				this.lambdaUpdate().eq(SysUserMenuRelation::getSysUserId, param.getSysUserId()).in(SysUserMenuRelation::getSysMenuId, deleteIds).remove();
			}
		}
		return Response.success();
	}

}
