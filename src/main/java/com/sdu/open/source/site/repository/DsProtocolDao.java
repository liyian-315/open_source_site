package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.DsProtocol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface DsProtocolDao {
    List<DsProtocol> findByIds(@Param("dsProtocolIds") Set<Long> dsProtocolIds);

    List<DsProtocol> findAll();
}
