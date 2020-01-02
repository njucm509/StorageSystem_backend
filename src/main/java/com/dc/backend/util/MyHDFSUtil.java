package com.dc.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class MyHDFSUtil {
    private static final String HDFS_PROPERTIES = "/hdfs.properties";

    private static String namenode;
    private static String fsPort;
    private static String defaultPath = "/input/";

    static {
        System.setProperty("hadoop.home.dir", "/usr/local/Cellar/hadoop/3.2.1");
        Properties p = new Properties();
        try {
            p.load(MyHDFSUtil.class.getResourceAsStream(HDFS_PROPERTIES));
            namenode = p.getProperty("namenode");
            fsPort = p.getProperty("fs_port");
            log.info("hdfs: {}:{}", namenode, fsPort);
        } catch (IOException e) {
            log.error("can not read from hdfs.properties:{}", e.getMessage());
        } finally {
//            createDefaultFilePath();
        }
    }

    private static void createDefaultFilePath() {
        FileSystem fs = getFileSystem();
        if (fs != null) {
            try {
                Path path = new Path(defaultPath);
                if (!fs.exists(path)) {
                    fs.mkdirs(path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createNewFile(String localFileAbsolutePath, String fileName) {
        FileSystem fs = getFileSystem();
        if (!Objects.isNull(fs)) {
            try {
                Path dst = new Path(defaultPath + fileName);
                if (fs.exists(dst)) {
                    fs.delete(dst, true);
                }
                fs.copyFromLocalFile(new Path(localFileAbsolutePath), new Path(defaultPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean findFileByFileName(String filename) {
        FileSystem fs = getFileSystem();
        boolean flag = false;
        if (!Objects.isNull(fs)) {
            Path path = new Path(defaultPath + filename);
            try {
                if (fs.exists(path)) {
                    flag = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static FileSystem getFileSystem() {
        Configuration configuration = new Configuration();
        configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        String path = "hdfs://" + namenode + ":" + fsPort;
        log.info("path: {}", path);
        configuration.set("fs.defaultFS", path);
        configuration.set("dfs.permissions", "false");
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSystem;
    }

    public static void main(String[] args) {
//        FileSystem fileSystem = getFileSystem();

//        log.info("{} init...", fileSystem);
        boolean b = findFileByFileName("test.csv");
        System.out.println(b);
    }
}
