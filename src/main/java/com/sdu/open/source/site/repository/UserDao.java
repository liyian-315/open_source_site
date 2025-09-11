package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserDao {
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 插入新用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int insert(User user);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Long id);

    void update(User user);
}