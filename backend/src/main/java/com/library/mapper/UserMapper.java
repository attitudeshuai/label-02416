package com.library.mapper;

import com.library.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问接口
 * SQL映射见 resources/mapper/UserMapper.xml
 */
@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Long id);

    int insert(User user);

    int update(User user);

    List<User> findAll();

    Long count();
}
