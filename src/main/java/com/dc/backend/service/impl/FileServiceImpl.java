package com.dc.backend.service.impl;

import com.dc.backend.service.FileService;
import com.dc.backend.util.MyFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    ServletContext context;

    public HashMap<String, String> handleFileUpload(MultipartFile file) {
        HashMap<String, String> res = new HashMap<>();
        String name = file.getOriginalFilename();
        String saveDir = context.getRealPath("/user/upload");
        log.info("saveDir is {}", saveDir);
        File file1 = new File(saveDir);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File[] files = file1.listFiles();
        for (File f : files) {
            if (f.getName().equals(name)) {
                res.put("msg", "exist");
                return res;
            }
        }
        try {
            Files.copy(file.getInputStream(), Paths.get(saveDir, name));
            res.put("msg", "ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public List<HashMap<String, Object>> listFile() {
        String filePath = context.getRealPath("/user/upload");
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        List<HashMap<String, Object>> res = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        if (files.length == 0) {
            map.put("msg", "no");
            res.add(map);
            return res;
        }
        int i = 1;
        for (File f : files) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("id", i++);
            data.put("filename", f.getName());
            res.add(data);
        }
        return res;
    }

    public List<HashMap<String, String>> getHeader(String filename) {
        List<HashMap<String, String>> list = new ArrayList<>();
        String filePath = context.getRealPath("/user/upload/");
        File file = new File(filePath + filename);
        log.info("{} process...", file.getName());
        String[] split = null;
        if (file.getName().endsWith("csv")) {
            try {
                split = MyFileUtil.readHeaderFromCsv(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.getName().endsWith("xls") | file.getName().endsWith("xlsx")) {
            try {
                split = MyFileUtil.readHeaderFromExcel(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (String s : split) {
            HashMap<String, String> map = new HashMap<>();
            map.put("content", s);
            map.put("encryption", "rsa");
            map.put("defaultEnc", "1");
            list.add(map);
        }
        return list;
    }

    public void delete(String filename) {
        String filePath = context.getRealPath("/user/upload");
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().equals(filename)) {
                f.delete();
            }
        }
    }
}
