package com.dc.backend.mapper;

import com.dc.backend.pojo.UserPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    UserPojo login(UserPojo user);

    void create(UserPojo user);

    void delete(@Param("id") Integer id);

    List<UserPojo> queryAll();

    UserPojo queryById(@Param("id") Integer id);

    void update(UserPojo userPojo);
}
