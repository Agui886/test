package mapper;

import entity.SysMenu;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 菜单信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2025-04-28
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {
	
	Set<String> selectAllMenuAjaxByUserId(Integer sysUserId);
	
	@Select(" <script>select menu.* "
			+ "from sys_menu as menu "
			+ "inner join sys_user_menu_relation as rm on menu.id = rm.sys_menu_id "
			+ "inner join sys_user as u on rm.sys_user_id = u.id "
			+ "where u.id = #{userId} <if test=\"title != null and title !='' \">menu.title=#{title}</if>order by menu.sort ASC</script>")
	List<SysMenu> selectAllMenuIdByUserId(@Param("userId") Integer id, @Param("title") String title);
}
