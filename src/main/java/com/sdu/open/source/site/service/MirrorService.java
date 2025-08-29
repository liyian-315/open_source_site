package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.Mirror;
import com.sdu.open.source.site.resposity.MirrorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-08-26  09:37
 * @Version: 1.0
 */
@Service
public class MirrorService {

    private MirrorDao mirrorDao;

    @Autowired
    private void setMirrorDao(MirrorDao mirrorDao) {
        this.mirrorDao = mirrorDao;
    }

    public List<Mirror> getMirrors() {
        return mirrorDao.selectAll();
    }
}
