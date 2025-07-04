package vo.manager;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import entity.CashoutRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CashoutRecordListVO {
	@ApiModelProperty("提现列表分页数据")
	private Page<CashoutRecord> page;
	@ApiModelProperty("总申请金额")
	private BigDecimal totalOrderAmount;
	@ApiModelProperty("总到账金额")
	private BigDecimal totalFinalAmount;
}	
