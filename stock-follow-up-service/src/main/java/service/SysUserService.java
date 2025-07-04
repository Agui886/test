package service;

import entity.SysUser;
import entity.common.Response;
import vo.manager.SysUserLoginResponseVO;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台系统账号表 服务类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
public interface SysUserService extends IService<SysUser> {
	Response<SysUserLoginResponseVO> login( String username, String loginPwd, String code, String ip);
	Response<Void> logout(String token, Integer sysUserId);
}
