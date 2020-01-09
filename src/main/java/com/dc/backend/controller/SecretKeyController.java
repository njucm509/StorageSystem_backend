package com.dc.backend.controller;

import com.dc.backend.mapper.SecretKeyMapper;
import com.dc.backend.pojo.SecretKey;
import com.dc.backend.service.SecretKeyService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class SecretKeyController {

    @Autowired
    SecretKeyService service;

    @RequestMapping("/key/user/{id}")
    public ResponseEntity<List<HashMap<String, String>>> getSecretKeyByUser(@PathVariable("id") Integer id) {
        List<HashMap<String, String>> res = service.getSecretKeyByUser(id);
        if (CollectionUtils.isEmpty(res)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(res);
        }

    }

}
