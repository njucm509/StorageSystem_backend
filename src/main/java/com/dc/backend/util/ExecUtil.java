package com.dc.backend.util;

import com.dc.backend.entity.DeviceInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExecUtil {
    public static List<DeviceInfo> getDeviceInfo() {
        List<DeviceInfo> list = new ArrayList<>();
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try {
            Process ps = Runtime.getRuntime().exec("ls /");
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
//            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine())!= null) {
                System.out.println(line);

//                sb.append(line).append("\n");
                String[] str = line.split("\\s+");
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        getDeviceInfo();
    }
}
