package com.dc.backend.mapper;

import com.dc.backend.pojo.UserPojo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    UserPojo login(UserPojo user);

    void create(UserPojo user);

    void delete(Integer id);

    List<UserPojo> queryAll();

    UserPojo queryById(Integer id);

    void update(UserPojo userPojo);
}
