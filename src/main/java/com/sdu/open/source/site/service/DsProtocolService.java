package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.DsProtocol;
import com.sdu.open.source.site.repository.DsProtocolDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class DsProtocolService {
    private DsProtocolDao dsProtocolDao;

    @Autowired
    public void setDsProtocolDao(DsProtocolDao dsProtocolDao) {
        this.dsProtocolDao = dsProtocolDao;
    }

    public List<DsProtocol> findByIds(Set<Long> dsProtocolIds) {
        if (dsProtocolIds == null || dsProtocolIds.isEmpty()) {
            return Collections.emptyList();
        }
        return dsProtocolDao.findByIds(dsProtocolIds);
    }
}
