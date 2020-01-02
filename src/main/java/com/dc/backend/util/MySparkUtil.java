package com.dc.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class MySparkUtil {
    private static final String SPARK_PROPERTIES = "/spark.properties";

    private static String master;
    private static String sparkPort;

    static {
        Properties p = new Properties();
        try {
            p.load(MySparkUtil.class.getResourceAsStream(SPARK_PROPERTIES));
            master = p.getProperty("master");
            sparkPort = p.getProperty("spark_port");
        } catch (IOException e) {
            log.error("can not read from spark.properties:{}", e.getMessage());
        }
    }

    public static JavaSparkContext getJavaSparkContext() {
        System.setProperty("hadoop.home.dir", "/usr/local/Cellar/hadoop/3.2.1");
        SparkConf conf = new SparkConf()
                .setAppName("Spark app")
                .setMaster("spark://127.0.0.1:7077");
        JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
    }

    public static void main(String[] args) {

    }
}
