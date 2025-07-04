package service;

import entity.SysUser;
import entity.SysUserMenuRelation;
import entity.common.Response;
import vo.manager.SysUserMenuSaveParamVO;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台账号权限配置表 服务类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
public interface SysUserMenuRelationService extends IService<SysUserMenuRelation> {

	Response<Void> saveSysUserMenuRelation(SysUserMenuSaveParamVO param, SysUser user);

}
