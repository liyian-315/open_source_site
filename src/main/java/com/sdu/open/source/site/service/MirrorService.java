package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.entity.Mirror;
import com.sdu.open.source.site.repository.MirrorDao;
import com.sdu.open.source.site.repository.MirrorMenuDao;
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
    private MirrorMenuDao mirrorMenuDao;

    @Autowired
    private void setMirrorDao(MirrorDao mirrorDao) {
        this.mirrorDao = mirrorDao;
    }

    @Autowired
    private void setMirrorMenuDao(MirrorMenuDao mirrorMenuDao) {
        this.mirrorMenuDao = mirrorMenuDao;
    }

    public List<Mirror> getMirrors(Mirror mirror) {
        return mirrorDao.selectAll(mirror);
    }

    public List<Menu> getMirrorsMenu() {
        return mirrorMenuDao.selectAll();
    }
}
