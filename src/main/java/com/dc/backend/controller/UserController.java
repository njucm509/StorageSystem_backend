package com.dc.backend.controller;

import com.dc.backend.pojo.UserPojo;
import com.dc.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService service;

//    @Autowired
//    DataSource dataSource;

    @RequestMapping("/login")
    public Map<String, Object> login(@RequestBody UserPojo user) {
//        System.out.println(dataSource);
        log.info("{} come in...", user);
        HashMap<String, Object> msg = new HashMap<>();
        UserPojo u = service.login(user);
        System.out.println(u);
        msg.put("msg", "ok");
        msg.put("user", u);
        return msg;
    }

    @RequestMapping("/user/create")
    public void create(@RequestBody UserPojo user) {
        log.info("user: {}", user);
        service.create(user);
    }

    @RequestMapping("/user/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        log.info("user_id: {}", id);
        service.delete(id);
    }

    @RequestMapping("/user/list")
    public List<UserPojo> queryAll() {
        List<UserPojo> list = service.queryAll();
        return list;
    }

    @RequestMapping("/user/{id}")
    public UserPojo queryById(@PathVariable("id") Integer id) {
        UserPojo user = service.queryById(id);
        return user;
    }

    @RequestMapping("/user/update")
    public void update(@RequestBody UserPojo userPojo) {
        service.update(userPojo);
    }

}
