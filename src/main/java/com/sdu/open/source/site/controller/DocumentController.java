package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.Document;
import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/docs")
    public ResponseEntity<List<Document>> getDocs(@RequestBody Document document) {
        try {
            List<Document> documents = documentService.getDocs(document);
            if (documents == null || documents.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(documents, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/docs_menu")
    public ResponseEntity<List<Menu>> getDocsMenu() {
        try {
            List<Menu> menus = documentService.getDocsMenu();
            if (menus == null || menus.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(menus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}