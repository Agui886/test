package entity.common;

import entity.SysUser;
import lombok.Data;


@Data
public class SysTokenVO {

    private String lastToken;

    private SysUser sysUser;
}
