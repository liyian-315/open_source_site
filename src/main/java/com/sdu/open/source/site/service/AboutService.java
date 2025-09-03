package com.sdu.open.source.site.service;

import com.sdu.open.source.site.repository.CopyWritingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AboutService {

    private CopyWritingDao copyWritingDao;

    @Autowired
    private void setCopyWritingDao(CopyWritingDao copyWritingDao) {
        this.copyWritingDao = copyWritingDao;
    }

    public String getAboutText(String area) {
        return copyWritingDao.selectCopyWritingTextByArea(area);
    }
}
