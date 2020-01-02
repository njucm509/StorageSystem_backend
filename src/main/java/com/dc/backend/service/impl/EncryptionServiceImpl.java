package com.dc.backend.service.impl;

import com.dc.backend.params.FileParam;
import com.dc.backend.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import java.io.File;

public class EncryptionServiceImpl implements EncryptionService {

    @Autowired
    ServletContext context;

    @Override
    public String encrypt(FileParam param) {
        String filename = param.getFilename();
        String realPath = context.getRealPath("/user/upload");
        File dir = new File(realPath);
        File[] files = dir.listFiles();
        File cur = null;
        for (File file : files) {
            if (file.getName().equals(filename)) {
                cur = file;
            }
        }
        if (cur == null) {
            return "no file";
        } else {
            return "";
        }

    }
}
