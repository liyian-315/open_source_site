package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.Document;
import com.sdu.open.source.site.resposity.DocumentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-08-25  16:33
 * @Version: 1.0
 */
@Service
public class DocumentService {

    private DocumentDao documentDao;

    @Autowired
    private void setDocumentDao(DocumentDao documentDao){
        this.documentDao = documentDao;
    }

    public List<Document> getDocs(Document document) {
        return documentDao.selectAll(document);
    }
}
