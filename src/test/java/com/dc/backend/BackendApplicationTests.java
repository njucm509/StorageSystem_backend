package com.dc.backend;

import com.dc.backend.params.FileHeaderParam;
import com.dc.backend.params.FileParam;
import com.dc.backend.pojo.User;
import com.dc.backend.service.EncryptionService;
import com.dc.backend.service.FileService;
import com.dc.backend.service.SecretKeyService;
import com.dc.backend.service.impl.FileServiceImpl;
import com.dc.backend.util.encrypt.aes.AESUtil;
import com.dc.backend.util.encrypt.rsa.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BackendApplicationTests {

    @Autowired
    FileService fileService;

    @Autowired
	EncryptionService encryptionService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testFileService() throws IOException {
//		List<HashMap<String, Object>> list = fileService.listFile();
//		System.out.println(list);

//		List<HashMap<String, String>> header = fileService.getHeader("test.csv");
//		System.out.println(header);

//		fileService.delete("test.csv");

    }

    @Test
    public void testEncrypt() throws IOException {
        FileParam param = new FileParam();
        User user = new User();
        user.setAccount("wangpeng");
        param.setUser(user);
        List<FileHeaderParam> list = new ArrayList<>();
        list.add(FileHeaderParam.builder().content("姓名").defaultEnc(1).encryption("rsa").build());
        list.add(FileHeaderParam.builder().content("学号").defaultEnc(1).encryption("aes").build());
        list.add(FileHeaderParam.builder().content("年龄").defaultEnc(0).encryption("rsa").build());
        param.setList(list);
        param.setFilename("test.csv");

		encryptionService.encrypt(param);
    }

    @Test
    public void testSecretKey() throws Exception {
//        new SecretKeyService().initKey(1);
        HashMap<String, HashMap<String, String>> map = new HashMap<>();

        HashMap<String, String> aes = new HashMap<>();
        aes.put("key", AESUtil.generateKey(1));
        map.put("aes", aes);

        HashMap<String, String> rsa = RSAUtil.generateKeyPair();
        map.put("rsa", rsa);

        log.info("user id -- {} -- aes_key: {} -- rsa_key: {}", 1, map.get("aes"), map.get("rsa"));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(map);
        log.info("db store key -- {}", jsonStr);

        HashMap map1 = objectMapper.readValue(jsonStr, HashMap.class);
        HashMap aes1 = (HashMap) map1.get("aes");
        String aesKey = (String) aes1.get("key");
        HashMap rsa1 = (HashMap) map1.get("rsa");
        String pubKey = (String) rsa1.get("publicKey");
        String privateKey = (String) rsa1.get("privateKey");

        log.info("aes key: {}---publicKey: {}---privateKey: {}", aesKey, pubKey, privateKey);
    }

}
