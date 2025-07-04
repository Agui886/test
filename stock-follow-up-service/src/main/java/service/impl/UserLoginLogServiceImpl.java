package service.impl;

import entity.UserLoginLog;
import mapper.UserLoginLogMapper;
import service.UserLoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员登录日志表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
@Service
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, UserLoginLog> implements UserLoginLogService {

}
