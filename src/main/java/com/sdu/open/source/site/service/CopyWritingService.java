package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.CopyWriting;
import com.sdu.open.source.site.enums.CopyWritingAreas;
import com.sdu.open.source.site.repository.CopyWritingDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CopyWritingService {

    private CopyWritingDao copyWritingDao;

    @Autowired
    public void setCopyWritingDao(CopyWritingDao copyWritingDao) {
        this.copyWritingDao = copyWritingDao;
    }

    public CopyWriting getCwByArea(String area) {
        return copyWritingDao.selectByArea(area);
    }

    public List<CopyWriting> getCwListByArea(String area) {
        return copyWritingDao.selectListByArea(area);
    }

    /**
     * 添加首页项目展示，限制最多4个
     */
    public boolean addHomeProject(CopyWriting copyWriting) {
        String area = CopyWritingAreas.HOME_PROJECT_DISPLAY.getCode();
        List<CopyWriting> existingProjects = copyWritingDao.selectListByArea(area);

        if (existingProjects != null && existingProjects.size() >= 4) {
            log.warn("首页项目展示已达上限（4个），无法添加新项目");
            return false;
        }

        copyWriting.setArea(area);
        copyWritingDao.insert(copyWriting);
        return true;
    }

    /**
     * 删除首页项目展示
     */
    public boolean deleteHomeProject(Long id) {
        try {
            copyWritingDao.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("删除首页项目展示失败，id: {}", id, e);
            return false;
        }
    }

    /**
     * 更新首页项目展示
     */
    public boolean updateHomeProject(CopyWriting copyWriting) {
        try {
            copyWriting.setArea(CopyWritingAreas.HOME_PROJECT_DISPLAY.getCode());
            copyWritingDao.update(copyWriting);
            return true;
        } catch (Exception e) {
            log.error("更新首页项目展示失败，id: {}", copyWriting.getId(), e);
            return false;
        }
    }

    /**
     * 添加首页活动展示，限制最多4个
     */
    public boolean addHomeEvent(CopyWriting copyWriting) {
        String area = CopyWritingAreas.HOME_EVENT_DISPLAY.getCode();
        List<CopyWriting> existingEvents = copyWritingDao.selectListByArea(area);

        if (existingEvents != null && existingEvents.size() >= 4) {
            log.warn("首页活动展示已达上限（4个），无法添加新活动");
            return false;
        }

        copyWriting.setArea(area);
        copyWritingDao.insert(copyWriting);
        return true;
    }

    /**
     * 删除首页活动展示
     */
    public boolean deleteHomeEvent(Long id) {
        try {
            copyWritingDao.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("删除首页活动展示失败，id: {}", id, e);
            return false;
        }
    }

    /**
     * 更新首页活动展示
     */
    public boolean updateHomeEvent(CopyWriting copyWriting) {
        try {
            copyWriting.setArea(CopyWritingAreas.HOME_EVENT_DISPLAY.getCode());
            copyWritingDao.update(copyWriting);
            return true;
        } catch (Exception e) {
            log.error("更新首页活动展示失败，id: {}", copyWriting.getId(), e);
            return false;
        }
    }
}
