package com.dc.backend.service;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.pojo.User;

import java.util.List;

public interface UserService {
    User login(User user);

    void create(User user);

    void delete(User user);

    List<User> queryAll();

    User queryById(Integer id);

    void update(User user);

    PageResult<User> queryUserByPageAndSort(PageParam pageParam);
}
