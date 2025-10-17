package com.sdu.open.source.site.repository;

import com.sdu.open.source.site.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<User> selectByUsernameOrFullname(@Param("user") User user, @Param("startIndex") Integer startIndex, @Param("pageSize") Integer pageSize);

    Long selectCountByUsernameOrFullname(@Param("user") User user);

    void deleteById(Long id);

    List<User> selectByIds(@Param("userIds") List<Long> userIds);
}