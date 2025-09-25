// dto/EventMetaDTO.java
package com.sdu.open.source.site.dto;
import lombok.Data;
import java.util.List;

@Data
public class EventMetaDTO {
    private List<TypeVO> types;
    private List<String> hotTags;
}
