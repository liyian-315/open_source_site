package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Document;
import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class DocumentController {

    private DocumentService documentService;

    @Autowired
    private void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/docs")
    public List<Document> getDocs(Document document) {
        return documentService.getDocs(document);
    }

    @GetMapping("/docs_menu")
    public List<Menu> getDocsMenu() {
        return documentService.getDocsMenu();
    }
}
