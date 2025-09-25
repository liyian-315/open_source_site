package com.sdu.open.source.site.service;
import com.sdu.open.source.site.entity.Event;
import com.sdu.open.source.site.entity.EventTemplate;
import com.sdu.open.source.site.repository.EventDao;
import com.sdu.open.source.site.repository.EventTemplateDao;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class EventService {
    @Resource private EventDao eventDao;
    @Resource private EventTemplateDao eventTemplateDao;

    public Map<String, Object> list(String q, String type, String tag, int page, int pageSize) {
        List<String> types = StringUtils.hasText(type) ? Arrays.asList(type.split(",")) : null;
        List<String> tags  = StringUtils.hasText(tag)  ? Arrays.asList(tag.split(","))  : null;
        int offset = Math.max(0, (page - 1) * pageSize);
        List<Event> list = eventDao.selectList(q, types, tags, offset, pageSize);
        int total = eventDao.countList(q, types, tags);
        return Map.of("list", list, "total", total);
    }

    public Map<String, Object> detail(String slug) {
        Event e = eventDao.selectBySlug(slug);
        if (e == null) return Map.of("event", null);
        EventTemplate t = null;
        if (StringUtils.hasText(e.getTemplateId())) {
            t = eventTemplateDao.selectById(e.getTemplateId());
        }
        return Map.of("event", e, "template", t);
    }

    public Long create(Event e) {
        eventDao.insert(e);
        return e.getId();
    }

    public int update(Event e) { return eventDao.update(e); }
}
