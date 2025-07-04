package vo.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockKChartVO {

    @ApiModelProperty(value = "东财数组字符串")
    private String[] candlestickChart;
}
