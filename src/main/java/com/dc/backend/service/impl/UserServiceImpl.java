package com.dc.backend.service.impl;

import com.dc.backend.mapper.UserMapper;
import com.dc.backend.pojo.UserPojo;
import com.dc.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper mapper;

    @Override
    public UserPojo login(UserPojo user) {
        return mapper.login(user);
    }

    @Override
    public void create(UserPojo user) {
        mapper.create(user);
    }

    @Override
    public void delete(Integer id) {
        mapper.delete(id);
    }

    @Override
    public List<UserPojo> queryAll() {
        return mapper.queryAll();
    }

    @Override
    public UserPojo queryById(Integer id) {
        return mapper.queryById(id);
    }

    @Override
    public void update(UserPojo userPojo) {
        mapper.update(userPojo);
    }
}
