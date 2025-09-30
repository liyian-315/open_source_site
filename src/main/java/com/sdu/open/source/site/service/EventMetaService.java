// service/EventMetaService.java
package com.sdu.open.source.site.service;

import com.sdu.open.source.site.dto.EventMetaDTO;
import com.sdu.open.source.site.vo.TypeVO;
import com.sdu.open.source.site.repository.EventMetaDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventMetaService {
    private final EventMetaDao eventMetaDao;

    public EventMetaDTO load() {
        List<TypeVO> types = eventMetaDao.selectTypes();
        List<String> hot = eventMetaDao.selectHotTags();
        EventMetaDTO dto = new EventMetaDTO();
        dto.setTypes(types);
        dto.setHotTags(hot);
        return dto;
    }
}
