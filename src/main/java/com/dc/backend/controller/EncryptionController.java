package com.dc.backend.controller;

import com.dc.backend.params.FileParam;
import com.dc.backend.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class EncryptionController {

    @Autowired
    EncryptionService encryptionService;

    @RequestMapping("/encrypt")
    public List<List<String>> encrypt(@RequestBody FileParam param) {
        log.info("filename: {} --list: {}", param.getFilename(), param.getList());
        List<List<String>> res = null;
        try {
            res = encryptionService.encrypt(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
