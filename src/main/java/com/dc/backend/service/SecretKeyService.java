package com.dc.backend.service;

import com.dc.backend.mapper.SecretKeyMapper;
import com.dc.backend.pojo.SecretKey;
import com.dc.backend.util.encrypt.aes.AESUtil;
import com.dc.backend.util.encrypt.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class SecretKeyService {

    @Autowired
    SecretKeyMapper mapper;

    public void initKey(Integer userId) throws Exception {
        SecretKey secretKey = new SecretKey();
        secretKey.setUserId(userId);
        SecretKey key = mapper.selectOne(secretKey);
        HashMap<String, HashMap<String, String>> map = new HashMap<>();
        if (key == null) {
            HashMap<String, String> aes = new HashMap<>();
            aes.put("key", AESUtil.generateKey(userId));
            map.put("aes", aes);

            HashMap<String, String> rsa = RSAUtil.generateKeyPair();
            map.put("rsa", rsa);

            log.info("user id -- {} -- aes_key: {} -- rsa_key: {}", userId, map.get("aes"), map.get("rsa"));

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(map);

            secretKey.setInitKey(jsonStr);
            log.info("db store key -- {}", jsonStr);
            int i = mapper.insertSelective(secretKey);
            if (i > 0) {
                log.info("user {} init key success ...", userId);
            } else {
                log.info("user {} already has key", userId);
            }
        }
    }

    public List<HashMap<String, String>> getSecretKeyByUser(Integer id) {
        SecretKey secretKey = new SecretKey();
        secretKey.setUserId(id);
        SecretKey key = mapper.selectOne(secretKey);
        String initKey = key.getInitKey();
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap map = null;
        try {
            map = objectMapper.readValue(initKey, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap aes = (HashMap) map.get("aes");
        String aesKey = (String) aes.get("key");
        HashMap rsa = (HashMap) map.get("rsa");
        String publicKey = (String) rsa.get("publicKey");
        String privateKey = (String) rsa.get("privateKey");

        HashMap<String, String> keyMap = new HashMap<>();
        keyMap.put("aes", aesKey);
        keyMap.put("rsa_publicKey", publicKey);
        keyMap.put("rsa_privateKey", privateKey);

        List<HashMap<String, String>> res = new ArrayList<>();
        res.add(keyMap);
        return res;
    }
}
