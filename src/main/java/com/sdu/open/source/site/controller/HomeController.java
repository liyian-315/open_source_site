package com.sdu.open.source.site.controller;

import com.sdu.open.source.site.entity.HomeCarousel;
import com.sdu.open.source.site.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@Slf4j
public class HomeController {

    private HomeService homeService;

    @Autowired
    private void setHomeService(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home_carousel")
    public ResponseEntity<List<HomeCarousel>> getHomeCarousel() {
        try {
            List<HomeCarousel> carousels = homeService.getHomeCarousels();

            if (carousels == null || carousels.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(carousels, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
