package com.sdu.open.source.site.service;

import com.sdu.open.source.site.entity.Menu;
import com.sdu.open.source.site.entity.Mirror;
import com.sdu.open.source.site.repository.MirrorDao;
import com.sdu.open.source.site.repository.MirrorMenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liyian
 * @Description:
 * @CreateTime: 2025-08-26  09:37
 * @Version: 1.0
 */
@Service
public class MirrorService {

    private MirrorDao mirrorDao;
    private MirrorMenuDao mirrorMenuDao;

    @Autowired
    private void setMirrorDao(MirrorDao mirrorDao) {
        this.mirrorDao = mirrorDao;
    }

    @Autowired
    private void setMirrorMenuDao(MirrorMenuDao mirrorMenuDao) {
        this.mirrorMenuDao = mirrorMenuDao;
    }

    public List<Mirror> getMirrors(Mirror mirror) {
        return mirrorDao.selectAll(mirror);
    }

    public List<Menu> getMirrorsMenu() {
        return mirrorMenuDao.selectAll();
    }

    // ==================== 设备管理 ====================

    /**
     * 获取指定架构下的所有设备（三级菜单）
     */
    public List<Menu> getDevicesByArch(String archTitle) {
        // 首先找到该架构对应的一级菜单项（level=0）
        List<Menu> allMenus = mirrorMenuDao.selectAll();
        Menu archMenu = allMenus.stream()
                .filter(menu -> menu.getLevel() != null
                        && menu.getLevel() == 0
                        && archTitle.equals(menu.getTitle()))
                .findFirst()
                .orElse(null);

        if (archMenu == null) {
            return null;
        }

        // 找到该架构下所有二级菜单
        List<Long> level2Ids = allMenus.stream()
                .filter(menu -> menu.getLevel() != null && menu.getLevel().equals(archMenu.getId()))
                .map(Menu::getId)
                .collect(Collectors.toList());

        if (level2Ids.isEmpty()) {
            return null;
        }

        // 找到所有三级菜单（设备）
        List<Menu> devices = allMenus.stream()
                .filter(menu -> menu.getLevel() != null && level2Ids.contains(menu.getLevel()))
                .collect(Collectors.toList());

        if (devices.isEmpty()) {
            return null;
        }
        return devices;
    }

    /**
     * 添加设备
     */
    @Transactional
    public String addDevice(Menu menu) {
        // 检查设备名是否重复
        if (mirrorMenuDao.checkDeviceExists(menu.getTitle(), null) > 0) {
            return "设备名已存在";
        }

        // 检查父级ID是否存在
        if (menu.getLevel() == null) {
            return "父级ID不能为空";
        }

        Menu parent = mirrorMenuDao.selectById(menu.getLevel());
        if (parent == null) {
            return "父级菜单不存在";
        }

        mirrorMenuDao.insertDevice(menu);
        return "success";
    }

    /**
     * 更新设备
     */
    @Transactional
    public String updateDevice(Menu menu) {
        // 检查设备名是否重复（排除自己）
        if (mirrorMenuDao.checkDeviceExists(menu.getTitle(), menu.getId()) > 0) {
            return "设备名已存在";
        }

        mirrorMenuDao.updateDevice(menu);
        return "success";
    }

    /**
     * 删除设备
     */
    @Transactional
    public String deleteDevice(Long id, String archTitle) {
        // 获取设备信息
        Menu device = mirrorMenuDao.selectById(id);
        if (device == null) {
            return "设备不存在";
        }

        String deviceTitle = device.getTitle();

        // 检查该设备下是否有镜像
        if (mirrorDao.countBySeriesAndArch(deviceTitle, archTitle) > 0) {
            return "该设备下存在镜像，无法删除";
        }

        mirrorMenuDao.deleteDevice(id);
        return "success";
    }

    // ==================== 镜像管理 ====================

    /**
     * 添加镜像
     */
    @Transactional
    public String addMirror(Mirror mirror) {
        // 检查镜像名是否重复
        if (mirrorDao.checkMirrorExists(mirror.getName(), null) > 0) {
            return "镜像名已存在";
        }

        // 自动设置当前时间
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        mirror.setTime(currentDate);

        mirrorDao.insertMirror(mirror);
        return "success";
    }

    /**
     * 更新镜像
     */
    @Transactional
    public String updateMirror(Mirror mirror) {
        // 检查镜像名是否重复（排除自己）
        if (mirrorDao.checkMirrorExists(mirror.getName(), mirror.getId()) > 0) {
            return "镜像名已存在";
        }

        // 自动更新时间
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        mirror.setTime(currentDate);

        mirrorDao.updateMirror(mirror);
        return "success";
    }

    /**
     * 删除镜像
     */
    @Transactional
    public String deleteMirror(Long id) {
        mirrorDao.deleteMirror(id);
        return "success";
    }
}
