package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.repository.CopyWritingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CopyWritingService {

    private CopyWritingDao copyWritingDao;

    @Autowired
    public void setCopyWritingDao(CopyWritingDao copyWritingDao) {
        this.copyWritingDao = copyWritingDao;
    }

    public CopyWriting getCwByArea(String area) {
        return copyWritingDao.selectByArea(area);
    }
}
