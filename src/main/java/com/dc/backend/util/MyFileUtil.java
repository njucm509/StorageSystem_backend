package com.dc.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class MyFileUtil {
    public static String[] readHeaderFromCsv(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line = br.readLine();
        String[] split = line.split(",");
        return split;
    }

    public static String[] readHeaderFromExcel(File file) throws IOException {
        FileOutputStream fos = null;
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new HSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        List<String> list = new ArrayList<>();
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            list.add(cellIterator.next().getStringCellValue());
        }
        return list.toArray(new String[0]);
    }

    public static boolean readCsvToHBaseBySpark(String HDFSPath, String filename) {
        boolean flag = false;
        JavaSparkContext sc = MySparkUtil.getJavaSparkContext();
        sc.textFile(HDFSPath);
        String tabeName = filename.split("\\.")[0];
        log.info("table: {}", tabeName);
        return flag;
    }

    public static void main(String[] args) {
        readCsvToHBaseBySpark("hdfs://127.0.0.1:8020/input/test.csv", "test.csv");
    }
}
