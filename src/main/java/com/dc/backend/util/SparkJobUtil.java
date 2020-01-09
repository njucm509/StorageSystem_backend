package com.dc.backend.util;

import com.dc.backend.params.FileHeaderParam;
import com.dc.backend.params.FileParam;
import com.dc.backend.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class SparkJobUtil implements Serializable {
    public static boolean readCsvToHBaseBySpark(String HDFSPath, FileParam param) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowDate = sdf.format(new Date());
        boolean flag = false;
        boolean hasTable = false;
        boolean[] isHeader = {true};
        String curEnc = null;
        String filename = param.getFilename();
        HashMap<String, String> map = ParamUtil.convert(param.getList());
        JavaSparkContext sc = MySparkUtil.getJavaSparkContext();
        sc.addJar("/Users/weilin/Documents/大创/backend/target/backend-0.0.1-SNAPSHOT.jar");
        String tableName = filename.split("\\.")[0] + nowDate + "_" + param.getUser().getAccount();
        log.info("table: {}", tableName);
        sc.hadoopConfiguration().set("hbase.zookeeper.quorum", "localhost:2181");
        sc.hadoopConfiguration().set("hbase.rootdir", "hdfs://localhost:8020/hbase");
        sc.hadoopConfiguration().set(TableOutputFormat.OUTPUT_TABLE, tableName);
        HBaseAdmin admin = new HBaseAdmin(sc.hadoopConfiguration());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        if (admin.tableExists(tableName)) {
            log.info("HBase --{} 已存在! 数据添加中...", tableName);
            hasTable = true;
        } else {
            log.info("HBase --{} 不存在...", tableName);

        }
        Job job = Job.getInstance(sc.hadoopConfiguration(), "spark--csv--HBase");
        job.setOutputFormatClass(TableOutputFormat.class);
        boolean finalHasTable = hasTable;
        final String[] headKey = {null};
        JavaPairRDD<ImmutableBytesWritable, Put> javaPairRDD = sc.textFile(HDFSPath).mapToPair(new PairFunction<String, ImmutableBytesWritable, Put>() {

            @Override
            public Tuple2<ImmutableBytesWritable, Put> call(String s) throws Exception {
                Put put = null;
                String[] split = s.split(",");
                log.info("==============split: {}", split);
                String[] family = new String[split.length];
                String[] qualifier = new String[split.length];
                // 第一列为rowKey
                put = new Put(Bytes.toBytes(split[0]));
                for (int i = 1; i < split.length; i++) {
                    if (isHeader[0]) {
                        headKey[0] = split[0];
                        if (split[i].indexOf(":") > 0) {
                            family[i] = split[i].substring(0, split[i].indexOf(":"));
                            qualifier[i] = split[i].substring(split[i].indexOf(":") + 1, split[i].length());
                        } else {
                            family[i] = qualifier[i] = split[i];
                        }
                    } else {
                        put.addColumn(family[i].getBytes(), qualifier[i].getBytes(), split[i].getBytes());
                        log.info("HBase insert {}--{}:{}---{}", tableName, family[i], qualifier[i], split[i]);
                    }
                }
                if (isHeader[0]) {
                    isHeader[0] = false;
                    if (!finalHasTable) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < family.length; i++) {
                            if (!list.contains(family[i])) {
                                list.add(family[i]);
                            }
                        }
                        for (String cf : list) {
                            desc.addFamily(new HColumnDescriptor(cf));
                        }
                        admin.createTable(desc);
                        log.info("HBase create table--{} 成功...", tableName);
                    }
                    return null;
                }
                return new Tuple2<>(new ImmutableBytesWritable(), put);
            }
        });

        log.info("----------------{}------------", javaPairRDD.count());

        javaPairRDD.saveAsNewAPIHadoopDataset(job.getConfiguration());

        HTable hTable = new HTable(sc.hadoopConfiguration(), tableName);
        Delete delete = new Delete(headKey[0].getBytes());
        hTable.delete(delete);
        return true;
    }

//    public static void main(String[] args) throws IOException {
////        System.out.println("abc".toUpperCase());
//        FileParam param = new FileParam();
//        User user = new User();
//        user.setAccount("wangpeng");
//        param.setUser(user);
//        List<FileHeaderParam> list = new ArrayList<>();
//        list.add(FileHeaderParam.builder().content("姓名").defaultEnc(1).encryption("rsa").build());
//        list.add(FileHeaderParam.builder().content("学号").defaultEnc(1).encryption("aes").build());
//        list.add(FileHeaderParam.builder().content("年龄").defaultEnc(0).encryption("rsa").build());
//        param.setList(list);
//        param.setFilename("test.csv");
//        readCsvToHBaseBySpark("/Users/weilin/Documents/大创/backend/file/test.csv", param);
//    }
}
