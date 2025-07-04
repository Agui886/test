package service;

import entity.IpAddress;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * ip信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
public interface IpAddressService extends IService<IpAddress> {
	IpAddress getIpAddress(String ip);
}
