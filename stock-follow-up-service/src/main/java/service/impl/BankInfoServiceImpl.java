package service.impl;

import entity.BankInfo;
import mapper.BankInfoMapper;
import service.BankInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 银行信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2025-05-09
 */
@Service
public class BankInfoServiceImpl extends ServiceImpl<BankInfoMapper, BankInfo> implements BankInfoService {

}
