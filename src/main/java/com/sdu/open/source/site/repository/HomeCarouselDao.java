package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.HomeCarousel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeCarouselDao {
    List<HomeCarousel> selectAll();
}
