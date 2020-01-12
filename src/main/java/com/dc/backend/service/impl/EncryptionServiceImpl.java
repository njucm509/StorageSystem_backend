package com.dc.backend.service.impl;

import com.dc.backend.config.HDFSConfig;
import com.dc.backend.params.FileHeaderParam;
import com.dc.backend.params.FileParam;
import com.dc.backend.pojo.EncRecord;
import com.dc.backend.service.EncRecordService;
import com.dc.backend.service.EncryptionService;
import com.dc.backend.service.SecretKeyService;
import com.dc.backend.util.MyFileUtil;
import com.dc.backend.util.ParamUtil;
import com.dc.backend.util.encrypt.MyEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService, Serializable {

    @Autowired
    transient ServletContext context;

    @Autowired
    transient SparkSession sparkSession;

    @Autowired
    private transient EncRecordService encRecordService;

    @Autowired
    transient SecretKeyService secretKeyService;

    private static final String SEPARATOR = File.separator;

    @Override
    public List<List<String>> encrypt(FileParam param) throws IOException {
        String filename = param.getFilename();
        String userFilePath = HDFSConfig.userFilePath;
        if (userFilePath == null) {
            userFilePath = "/input";
        }
        List<String> encOptions = new ArrayList<String>() {
            {
                this.add("RSA");
                this.add("AES");
                this.add("ECC");
                this.add("MD5");
            }
        };

        List<HashMap<String, List<String>>> tmp = new ArrayList<>();
        List<List<String>> res = new ArrayList<>();
        List<FileHeaderParam> list = param.getList();
        List<String> colNames = list.stream().map(FileHeaderParam::getContent).collect(Collectors.toList());
        HashMap<String, String> convert = ParamUtil.convert(list);

        ObjectMapper objectMapper = new ObjectMapper();
        // 操作记录
        EncRecord record = new EncRecord();
        record.setUserId(param.getUser().getId());
        record.setFilename(param.getFilename());
        record.setRecord(objectMapper.writeValueAsString(convert));
        encRecordService.create(record);

        // 查找密钥
        List<HashMap<String, String>> keyList = secretKeyService.getSecretKeyByUser(param.getUser().getId());

        log.info("key list: {}", keyList);

        System.out.println(colNames);
        sparkSession.udf().register("encryptByAES", new UDF1<String, String>() {
            @Override
            public String call(String s) throws Exception {
                log.info("s: {} ", s);
                return MyEncryptUtil.encrypt(s, "aes", keyList);
            }
        }, DataTypes.StringType);

        sparkSession.udf().register("encryptByECC", new UDF1<String, String>() {
            @Override
            public String call(String s) throws Exception {
                log.info("s: {} ", s);
                return MyEncryptUtil.encrypt(s, "ecc", keyList);
            }
        }, DataTypes.StringType);

        sparkSession.udf().register("encryptByMD5", new UDF1<String, String>() {
            @Override
            public String call(String s) throws Exception {
                log.info("s: {} ", s);
                return MyEncryptUtil.encrypt(s, "md5", keyList);
            }
        }, DataTypes.StringType);

        sparkSession.udf().register("encryptByRSA", new UDF1<String, String>() {
            @Override
            public String call(String s) throws Exception {
                log.info("s: {} ", s);
                return MyEncryptUtil.encrypt(s, "rsa", keyList);
            }
        }, DataTypes.StringType);

        sparkSession.udf().register("encryptByNO", new UDF1<String, String>() {
            @Override
            public String call(String s) throws Exception {
                log.info("s: {} ", s);
                return s;
            }
        }, DataTypes.StringType);
        Dataset<Row> csv = null;
        if (filename.endsWith("csv")) {
            csv = sparkSession.read().option("header", "true").csv(userFilePath + SEPARATOR + filename);
        }
        if (filename.endsWith("xls") || filename.endsWith("xlsx")) {

        }
        csv.createOrReplaceTempView("temp");
        SQLContext context = csv.sqlContext();
        for (String colName : colNames) {
            char[] chars = colName.toCharArray();
            System.out.println("colName: " + chars.length);
            String realColName = "";
            for (int i = 0; i < chars.length; i++) {
                if (i == 0 && (byte) chars[0] == -1) {
                    continue;
                }
                realColName += chars[i];
            }
            System.out.println("realColName: " + realColName.length());
            log.info("当前列名:{}", colName);
            HashMap<String, List<String>> map = new HashMap<>();
            String enc = convert.get(colName).toUpperCase();
            Dataset<Row> col = null;
            if (encOptions.contains(enc)) {
                col = context.sql("select encryptBy" + enc + "(`" + realColName + "`) from temp");
            } else if (enc.equals("NO")) {
                col = context.sql("select encryptBy" + enc + "(`" + realColName + "`) from temp");
            }
            col.show();
            List<Row> rows = col.collectAsList();
            List<String> inner = new ArrayList<>();
            for (Row row : rows) {
                inner.add((String) row.get(0));
            }
            map.put(colName, inner);
            tmp.add(map);
        }
        log.info("tmp: {}", tmp);
        // 列转行
        List<String> headers = new ArrayList<>();
        List<List<String>> con = new ArrayList<>();
        for (HashMap<String, List<String>> in : tmp) {
            Map.Entry<String, List<String>> next = in.entrySet().iterator().next();
            headers.add(next.getKey());
            con.add(next.getValue());
        }
        String[][] str = new String[con.get(0).size()][con.size()];
        for (int i = 0; i < con.size(); i++) {
            List<String> cur = con.get(i);
//                str = new String[cur.size()][];
            for (int j = 0; j < cur.size(); j++) {
//                    str[j] = new String[con.size()];
                log.debug("cur ..{}", cur.get(j));
                str[j][i] = cur.get(j);
                System.out.println(str[j][i]);
            }
        }
        res.add(headers);
        for (int i = 0; i < str.length; i++) {
            res.add(Arrays.asList(str[i]));
        }
        log.info("res: {}", res);
        return res;
    }
}
