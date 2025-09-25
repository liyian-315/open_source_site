package com.sdu.open.source.site.repository;
import com.sdu.open.source.site.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EventDao {
    List<Event> selectList(@Param("q") String q,
                           @Param("types") List<String> types,
                           @Param("tags") List<String> tags,
                           @Param("offset") int offset,
                           @Param("limit") int limit);
    int countList(@Param("q") String q,
                  @Param("types") List<String> types,
                  @Param("tags") List<String> tags);
    Event selectBySlug(@Param("slug") String slug);
    int insert(Event e);
    int update(Event e);
}
