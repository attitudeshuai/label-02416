package com.library.mapper;

import com.library.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * User Mapper Interface
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO sys_user (username, password, nickname, email, phone, role, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{phone}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE sys_user SET nickname = #{nickname}, email = #{email}, phone = #{phone}, " +
            "avatar = #{avatar} WHERE id = #{id}")
    int update(User user);

    @Select("SELECT * FROM sys_user WHERE status = 1")
    List<User> findAll();

    @Select("SELECT COUNT(*) FROM sys_user")
    Long count();
}
