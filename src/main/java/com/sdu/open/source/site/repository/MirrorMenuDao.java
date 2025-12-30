package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-09-01  19:54
 * @Version: 1.0
 */
@Mapper
public interface MirrorMenuDao {
    List<Menu> selectAll();

    // 根据父级ID查询子节点
    List<Menu> selectByParentId(@Param("parentId") Long parentId);

    // 根据ID查询单个菜单
    Menu selectById(@Param("id") Long id);

    // 添加设备
    int insertDevice(Menu menu);

    // 更新设备
    int updateDevice(Menu menu);

    // 删除设备（根据ID）
    int deleteDevice(@Param("id") Long id);

    // 检查设备名是否存在
    int checkDeviceExists(@Param("title") String title, @Param("excludeId") Long excludeId);
}
