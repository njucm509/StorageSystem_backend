package com.dc.backend.service;

import com.dc.backend.entity.DeviceInfo;

import java.io.IOException;

public interface MonitorService {
    DeviceInfo getDeviceInfo(String name) throws IOException, InterruptedException;
}
