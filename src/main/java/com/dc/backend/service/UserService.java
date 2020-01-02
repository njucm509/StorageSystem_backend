package com.dc.backend.service;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.pojo.UserPojo;

import java.util.List;

public interface UserService {
    UserPojo login(UserPojo user);

    void create(UserPojo user);

    void delete(UserPojo userPojo);

    List<UserPojo> queryAll();

    UserPojo queryById(Integer id);

    void update(UserPojo userPojo);

    PageResult<UserPojo> queryUserByPageAndSort(PageParam pageParam);
}
