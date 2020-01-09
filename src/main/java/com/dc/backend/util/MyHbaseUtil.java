package com.dc.backend.util;

import com.dc.backend.entity.HbaseRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MyHbaseUtil {
    /**
     * 连接池对象
     */
    private static final String ZK_QUORUM = "hbase.zookeeper.quorum";
    private static final String ZK_CLIENT_PORT = "hbase.zookeeper.property.clientPort";
    /**
     * HBase位置
     */
    private static final String HBASE_POS = "localhost";

    /**
     * ZooKeeper位置
     */
    private static final String ZK_POS = "localhost";

    /**
     * zookeeper服务端口
     */
    private final static String ZK_PORT_VALUE = "2181";

    public static String getZkQuorum() {
        return ZK_QUORUM;
    }

    public static String getZkClientPort() {
        return ZK_CLIENT_PORT;
    }

    public static String getHbasePos() {
        return HBASE_POS;
    }

    public static String getZkPos() {
        return ZK_POS;
    }

    public static String getZkPortValue() {
        return ZK_PORT_VALUE;
    }

    private static Configuration configuration;

    static {
        System.setProperty("hadoop.home.dir", "/usr/local/Cellar/hadoop/3.2.1");
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.rootdir", "hdfs://" + HBASE_POS
                + ":8020/hbase");
        configuration.set(ZK_QUORUM, ZK_POS);
        configuration.set(ZK_CLIENT_PORT, ZK_PORT_VALUE);
    }

    public static Connection getConnection() throws IOException {
        return ConnectionFactory.createConnection(configuration);
    }

    /**
     * 判断表是否存在
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean isTableExist(String tableName) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        return admin.tableExists(TableName.valueOf(tableName));
    }

    /**
     * 创建表
     *
     * @param tableName
     * @param columnFamily
     * @return
     * @throws IOException
     */
    public static boolean createTable(String tableName, String... columnFamily) throws IOException {
        boolean flag = false;
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        TableName table = TableName.valueOf(tableName);
        if (admin.tableExists(table)) {
            log.info("HBase --{} 原数据库中表已存在！", tableName);
        } else {
            HTableDescriptor descriptor = new HTableDescriptor(table);
            for (String cf : columnFamily) {
                descriptor.addFamily(new HColumnDescriptor(cf));
            }
            admin.createTable(descriptor);
            log.info("HBase --{} 创建成功...", tableName);
            flag = true;
        }
        return flag;
    }

    /**
     * 删除表
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean dropTable(String tableName) throws IOException {
        boolean flag = false;
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        TableName table = TableName.valueOf(tableName);
        if (isTableExist(tableName)) {
            admin.disableTable(table);
            admin.deleteTable(table);
            log.info("HBase --{} 删除成功...", tableName);
            flag = true;
        } else {
            log.info("HBase --{} 表不存在！", tableName);
        }
        return flag;
    }

    /**
     * 插入数据
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param column
     * @param value
     * @return
     * @throws IOException
     */
    public static boolean addRowData(String tableName, String rowKey, String columnFamily, String column, String value) throws IOException {
        boolean flag = true;
        if (!isTableExist(tableName)) {
            log.info("HBase --insert--{} 表不存在! ", tableName);
            flag = false;
        } else {
            HTable hTable = new HTable(configuration, tableName);
            Put put = new Put(rowKey.getBytes());
            put.add(columnFamily.getBytes(), column.getBytes(), value.getBytes());
            hTable.put(put);
            hTable.close();
            log.info("HBase --insert --{} -- {}:{}--{} 成功...", tableName, columnFamily, column, value);
        }
        return flag;
    }

    /**
     * 删除多行数据
     *
     * @param tableName
     * @param rows
     * @return
     * @throws IOException
     */
    public static boolean deleteMultiRows(String tableName, String... rows) throws IOException {
        boolean flag = true;
        if (!isTableExist(tableName)) {
            flag = false;
        } else {
            HTable hTable = new HTable(configuration, tableName);
            List<Delete> deleteList = new ArrayList<>();
            for (String row : rows) {
                Delete delete = new Delete(row.getBytes());
                deleteList.add(delete);
            }
            hTable.delete(deleteList);
            hTable.close();
            log.info("HBase --deleteRows --{} -- {} 成功...", tableName, rows);
        }
        return flag;
    }

    public static List<HbaseRow> getAllRows(String tableName) throws IOException {
        HTable hTable = new HTable(configuration, tableName);
        Scan scan = new Scan();
        ResultScanner results = hTable.getScanner(scan);
        List<HbaseRow> list = new ArrayList<>();
        for (Result result : results) {
            Cell[] cells = result.rawCells();
            HbaseRow row = new HbaseRow();
            for (Cell cell : cells) {
                row.setRowKey(Bytes.toString(CellUtil.cloneRow(cell)));
                row.setColumnFamily(Bytes.toString(CellUtil.cloneFamily(cell)));
                row.setColumn(Bytes.toString(CellUtil.cloneQualifier(cell)));
                row.setValue(Bytes.toString(CellUtil.cloneValue(cell)));
                row.setTime(cell.getTimestamp());
            }
            log.info("row -- {}", row);
            list.add(row);
        }
        return list;
    }

    /**
     * 获取一行
     *
     * @param tableName
     * @param rowKey
     * @return
     * @throws IOException
     */
    public static HbaseRow getRow(String tableName, String rowKey) throws IOException {
        HTable hTable = new HTable(configuration, tableName);
        Get get = new Get(rowKey.getBytes());
//        get.setMaxVersions();
//        get.setTimeStamp();
        Result result = hTable.get(get);
        HbaseRow row = getByResult(result);
        return row;
    }

    public static HbaseRow getRowQualifier(String tableName, String rowKey, String columnFamily, String qualifier) throws IOException {
        HTable table = new HTable(configuration, tableName);
        Get get = new Get(rowKey.getBytes());
        get.addColumn(columnFamily.getBytes(), qualifier.getBytes());
        Result result = table.get(get);
        HbaseRow row = getByResult(result);
        return row;
    }

    public static HbaseRow getByResult(Result result) {
        HbaseRow row = new HbaseRow();
        for (Cell cell : result.rawCells()) {
            row.setRowKey(Bytes.toString(result.getRow()));
            row.setColumnFamily(Bytes.toString(CellUtil.cloneFamily(cell)));
            row.setColumn(Bytes.toString(CellUtil.cloneQualifier(cell)));
            row.setValue(Bytes.toString(CellUtil.cloneValue(cell)));
            row.setTime(cell.getTimestamp());
        }
        log.info("row: {}", row);
        return row;
    }

    public static void main(String[] args) throws IOException {
//        MyHbaseUtil myHbaseUtil = new MyHbaseUtil();
//        System.out.println(MyHbaseUtil.isTableExist("stu"));
//        createTable("stu");

//        addRowData("stu", "123", "info", "123", "123");
        List<HbaseRow> list = getAllRows("stu");
        System.out.println(list);
//        List<Environment.Entry> entries = Environment.list();
//        System.out.println(entries);

//        HbaseRow row = getRow("stu", "123");
//        System.out.println(row);
    }
}
