package com.dc.backend.service;

import com.dc.backend.pojo.UserPojo;

import java.util.List;

public interface UserService {
    public UserPojo login(UserPojo user);

    void create(UserPojo user);

    void delete(Integer id);

    List<UserPojo> queryAll();

    UserPojo queryById(Integer id);

    void update(UserPojo userPojo);
}
