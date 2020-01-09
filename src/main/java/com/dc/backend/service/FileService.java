package com.dc.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface FileService {
    HashMap<String, String> handleFileUpload(MultipartFile file) throws IOException;

    List<HashMap<String, Object>> listFile() throws IOException;

    List<HashMap<String, String>> getHeader(String filename) throws IOException;

    void delete(String filename) throws IOException;
}
