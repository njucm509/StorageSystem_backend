package com.dc.backend.controller;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.pojo.User;
import com.dc.backend.service.SecretKeyService;
import com.dc.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService service;

    @Autowired
    SecretKeyService secretKeyService;

    @RequestMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
//        System.out.println(dataSource);
        log.info("{} come in...", user);
        HashMap<String, Object> msg = new HashMap<>();
        User u = service.login(user);
        System.out.println(u);
        msg.put("msg", "ok");
        msg.put("user", u);
        return msg;
    }

    @RequestMapping("/user/create")
    public User create(@RequestBody User user) {
        log.info("user: {}", user);
        service.create(user);
        User res = service.login(user);
        try {
            secretKeyService.initKey(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @RequestMapping("/user/multi")
    public ResponseEntity<Void> multi(MultipartFile file) {
        log.info("user: {}", file.getOriginalFilename());
        try {
            service.multi(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/user/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        log.info("user_id: {}", id);
        User user = new User();
        user.setId(id);
        service.delete(user);
    }

    @RequestMapping("/user/list")
    public List<User> queryAll() {
        List<User> list = service.queryAll();
        return list;
    }

    @GetMapping("/user/page")
    public ResponseEntity<PageResult<User>> queryUserByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                            @RequestParam(value = "sortBy", required = false) String sortBy,
                                                            @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                                            @RequestParam(value = "key", required = false) String key) {
        PageParam pageParam = PageParam.builder().page(page).rows(rows).sortBy(sortBy).desc(desc).key(key).build();
        log.info("pageParam: {}", pageParam);
        PageResult<User> result = service.queryUserByPageAndSort(pageParam);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping("/user/{id}")
    public User queryById(@PathVariable("id") Integer id) {
        User user = service.queryById(id);
        return user;
    }

    @RequestMapping("/user/update")
    public void update(@RequestBody User user) {
        log.info("user id: {} update ...", user.getId());
        service.update(user);
    }

//    @RequestMapping("/user/down")
//    public ResponseEntity<byte[]> down(String filename) {
//        log.info("down {}...", filename);
//        String path = ClassUtils.getDefaultClassLoader().getResource("static/file").getPath();
//        File f = new File(path);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDispositionFormData("attachment", filename);
//        ResponseEntity<byte[]> res = null;
//        try {
//            res = new ResponseEntity<>(FileUtils.readFileToByteArray(new File(path, filename)), headers, HttpStatus.CREATED);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return res;
//    }

    @RequestMapping("/user/down")
    public ResponseEntity<byte[]> down() {
        log.info("down user.csv");
        String colNames = "姓名,身份证号,身高,体重,年龄";
        String data = "张三,320202199811113333,176,120,22";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "user.csv");
        ResponseEntity<byte[]> res = null;
        try {
            File tempFile = File.createTempFile("user", "csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
            log.info("col : {}", colNames);
            bw.write(colNames);
            bw.newLine();
            log.info("data : {}", data);
            bw.write(data);
            bw.flush();
            bw.close();
            res = new ResponseEntity<>(FileUtils.readFileToByteArray(tempFile), headers, HttpStatus.CREATED);
            boolean b = tempFile.delete();
            log.info("temp file delete {} ...", b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

}
