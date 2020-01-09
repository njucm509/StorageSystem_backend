package com.dc.backend.mapper;

import com.dc.backend.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserMapper extends Mapper<User> {
    User login(User user);

//    void create(UserPojo user);
//
//    void delete(@Param("id") Integer id);
//
//    List<UserPojo> queryAll();
//
//    UserPojo queryById(@Param("id") Integer id);
//
    void update(User user);
}
