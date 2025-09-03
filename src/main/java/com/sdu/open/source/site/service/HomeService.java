package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.HomeCarousel;
import com.sdu.open.source.site.repository.HomeCarouselDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {
    private HomeCarouselDao homeCarouselDao;

    @Autowired
    public void setHomeCarouselDao(HomeCarouselDao homeCarouselDao) {
        this.homeCarouselDao = homeCarouselDao;
    }

    public List<HomeCarousel> getHomeCarousels() {
        return homeCarouselDao.selectAll();
    }
}
