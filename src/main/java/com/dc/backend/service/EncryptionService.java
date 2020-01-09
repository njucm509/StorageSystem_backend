package com.dc.backend.service;

import com.dc.backend.params.FileParam;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface EncryptionService extends Serializable {
    List<List<String>> encrypt(FileParam param) throws IOException;
}

