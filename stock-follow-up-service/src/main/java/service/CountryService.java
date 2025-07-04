package service;

import entity.Country;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 国家地区信息表 服务类
 * </p>
 *
 * @author 
 * @since 2025-05-08
 */
public interface CountryService extends IService<Country> {

	List<Country> countryList();
	
	Country getCountryByAreaCode(String areaCode);

}
