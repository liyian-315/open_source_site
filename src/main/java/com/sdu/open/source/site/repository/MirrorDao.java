package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.Mirror;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MirrorDao {
    List<Mirror> selectAll(Mirror mirror);

    // 添加镜像
    int insertMirror(Mirror mirror);

    // 更新镜像
    int updateMirror(Mirror mirror);

    // 删除镜像
    int deleteMirror(@Param("id") Long id);

    // 根据series查询镜像数量
    int countBySeriesAndArch(@Param("series") String series, @Param("arch") String arch);

    // 检查镜像名是否存在
    int checkMirrorExists(@Param("name") String name, @Param("excludeId") Long excludeId);
}
