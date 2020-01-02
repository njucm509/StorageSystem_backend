package com.dc.backend.service;

import com.dc.backend.params.FileParam;
import org.springframework.stereotype.Service;

@Service
public interface EncryptionService {
    String encrypt(FileParam param);
}

