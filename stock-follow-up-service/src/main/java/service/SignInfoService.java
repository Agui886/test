package service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import entity.SignInfo;
import entity.common.Response;
import vo.manager.SignListSearchParamVO;

/**
 * <p>
 * 跟投信号表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface SignInfoService extends IService<SignInfo> {
	
	/**
	 * 跟投管理-跟投信号
	 * @param page
	 * @param param
	 */
	void managerList(Page<SignInfo> page, SignListSearchParamVO param);
	
	/**
	 * 跟投信号-发布信号
	 * @param signInfo
	 * @param publisher
	 * @return
	 */
	Response<Void> release(SignInfo signInfo, String publisher);
	
	/**
	 * 跟投信号-配置信号
	 * @param signInfo
	 * @return
	 */
	Response<Void> configure(SignInfo signInfo);
}
