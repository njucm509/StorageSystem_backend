package com.dc.backend.controller;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.pojo.UserPojo;
import com.dc.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        UserPojo user = new UserPojo();
        user.setId(id);
        service.delete(user);
    }

    @RequestMapping("/user/list")
    public List<UserPojo> queryAll() {
        List<UserPojo> list = service.queryAll();
        return list;
    }

    @GetMapping("/user/page")
    public ResponseEntity<PageResult<UserPojo>> queryUserByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                                 @RequestParam(value = "sortBy", required = false) String sortBy,
                                                                 @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                                                 @RequestParam(value = "key", required = false) String key) {
        PageParam pageParam = PageParam.builder().page(page).rows(rows).sortBy(sortBy).desc(desc).key(key).build();
        log.info("pageParam: {}", pageParam);
        PageResult<UserPojo> result = service.queryUserByPageAndSort(pageParam);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
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
