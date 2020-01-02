package com.dc.backend.mapper;

import com.dc.backend.pojo.UserPojo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserMapper extends Mapper<UserPojo> {
    UserPojo login(UserPojo user);

//    void create(UserPojo user);
//
//    void delete(@Param("id") Integer id);
//
//    List<UserPojo> queryAll();
//
//    UserPojo queryById(@Param("id") Integer id);
//
    void update(UserPojo userPojo);
}
