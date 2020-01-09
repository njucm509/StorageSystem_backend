package com.dc.backend.service.impl;

import com.dc.backend.config.HDFSConfig;
import com.dc.backend.service.FileService;
import com.dc.backend.util.MyFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
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

    private static final String SEPARATOR = File.separator;

    @Autowired
    FileSystem fileSystem;

    public HashMap<String, String> handleFileUpload(MultipartFile file) throws IOException {
        String userFilePath = HDFSConfig.userFilePath;
        if (userFilePath == null) {
            userFilePath = "/input";
        }
        HashMap<String, String> res = new HashMap<>();
        Path path = new Path(userFilePath);
        if (!fileSystem.exists(path)) {
            fileSystem.mkdirs(path);
        }
        Path filePath = new Path(userFilePath + SEPARATOR + file.getOriginalFilename());
        if (fileSystem.exists(filePath)) {
            res.put("msg", "文件已存在!");
            return res;
        }
        FSDataOutputStream fos = fileSystem.create(filePath, true);
        IOUtils.copyBytes(file.getInputStream(), fos, 4096, true);

        res.put("msg", file.getOriginalFilename() + " 文件上传成功!");
        
        return res;
    }

    public List<HashMap<String, Object>> listFile() throws IOException {
        String userFilePath = HDFSConfig.userFilePath;
        if (userFilePath == null) {
            userFilePath = "/input";
        }
        Path path = new Path(userFilePath);
        if (!fileSystem.exists(path)) {
            fileSystem.mkdirs(path);
        }
        RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(path, false);
        List<HashMap<String, Object>> res = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        if (!iterator.hasNext()) {
            map.put("msg", "no");
            res.add(map);
            return res;
        }
        int i = 1;
        while (iterator.hasNext()) {
            LocatedFileStatus fileStatus = iterator.next();
            Path filePath = fileStatus.getPath();
            log.info("file path: {}", filePath.getName());
            HashMap<String, Object> data = new HashMap<>();
            data.put("id", i++);
            data.put("filename", filePath.getName());
            res.add(data);
        }
        return res;
    }

    public List<HashMap<String, String>> getHeader(String filename) throws IOException {
        List<HashMap<String, String>> list = new ArrayList<>();
        String filePath = HDFSConfig.userFilePath;
        if (filePath == null) {
            filePath = "/input";
        }
        Path path = new Path(filePath + SEPARATOR + filename);
        log.info("file path: {}", path);
        FSDataInputStream fis = fileSystem.open(path);
        String[] sTmp = filename.split("\\.");
        log.info("file prefix: {} -- suffix: {}", sTmp[0], sTmp[1]);
        File tmp = File.createTempFile(sTmp[0], sTmp[1]);
        FileOutputStream fos = new FileOutputStream(tmp);
        IOUtils.copyBytes(fis, fos, 4096, true);
        if (tmp.length() <= 0) {
            return list;
        }
        log.info("{} process...", tmp.getName());
        String[] split = null;
        if (tmp.getName().endsWith("csv")) {
            try {
                split = MyFileUtil.readHeaderFromCsv(tmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (tmp.getName().endsWith("xls") | tmp.getName().endsWith("xlsx")) {
            try {
                split = MyFileUtil.readHeaderFromExcel(tmp);
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
        boolean b = tmp.delete();
        log.info("delete tmp file {}", b);
        return list;
    }

    public void delete(String filename) throws IOException {
        String filePath = HDFSConfig.userFilePath;
        if (filePath == null) {
            filePath = "/input";
        }
        Path path = new Path(filePath + SEPARATOR + filename);
        boolean b = fileSystem.delete(path, true);
        log.info("delete {} -- {}", filename, b);
    }
}
