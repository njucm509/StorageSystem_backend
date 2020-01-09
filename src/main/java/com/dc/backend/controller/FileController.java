package com.dc.backend.controller;

import com.dc.backend.params.FileParam;
import com.dc.backend.service.impl.FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class FileController {


    @Autowired
    FileServiceImpl fileService;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @RequestMapping("/file/upload")
    public HashMap<String, String> handleFileUpload(MultipartFile file) {
        log.info("{} enter...", file.getOriginalFilename());
        HashMap<String, String> res = null;
        try {
            res = fileService.handleFileUpload(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 返回文件列表
     *
     * @return
     */
    @RequestMapping("/file/list")
    public List<HashMap<String, Object>> listFile() {
        List<HashMap<String, Object>> res = null;
        try {
            res =  fileService.listFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @RequestMapping("/file/header")
    public List<HashMap<String, String>> getHeader(@RequestBody FileParam param) {
        log.info("{} get header...", param.getFilename());
        List<HashMap<String, String>> res = null;
        try {
            res = fileService.getHeader(param.getFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 删除文件
     *
     * @param filename
     */
    @RequestMapping("file/delete/{filename}")
    public void deleteFile(@PathVariable("filename") String filename) {
        try {
            fileService.delete(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
