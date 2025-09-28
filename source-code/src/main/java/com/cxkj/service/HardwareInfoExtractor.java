package com.cxkj.service;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HardwareInfoExtractor {


    public static void main(String[] args) {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();

        String processorID = processor.getProcessorIdentifier().getProcessorID();


        System.out.println("processorID is " + processorID);
        System.out.println("getMotherboardModel is " + getMotherboardModel());
        System.out.println("getAllMacAddresses is " + getAllMacAddresses());

        System.out.println("所有IP地址：");
        getAllIpAddresses().forEach(System.out::println);

        System.out.println("\nIPv4地址：");
        getIpv4Addresses().forEach(System.out::println);

        System.out.println("\nIPv6地址：");
        getIpv6Addresses().forEach(System.out::println);
    }

    /**
     * 获取系统中所有网络接口的MAC地址（包括物理网卡和虚拟网卡）
     *
     * @return MAC地址列表，每个地址格式为XX:XX:XX:XX:XX:XX
     */
    public static List<String> getAllMacAddresses() {
        List<String> macAddresses = new ArrayList<>();

        // 获取系统硬件信息
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();

        // 获取所有网络接口
        List<NetworkIF> networkIFs = hardware.getNetworkIFs();

        for (NetworkIF networkIF : networkIFs) {
            String macAddr = networkIF.getMacaddr();
            // 过滤无效MAC地址（空值或全0）
            if (macAddr != null && !macAddr.trim().isEmpty() && !macAddr.equals("00:00:00:00:00:00")) {
                macAddresses.add(macAddr);
            }
        }

        return macAddresses;
    }

    /**
     * 获取主板型号
     *
     * @return 主板型号字符串，获取失败返回null
     */
    public static String getMotherboardModel() {
        // 获取系统信息实例
        SystemInfo systemInfo = new SystemInfo();
        // 获取硬件抽象层
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        // 获取计算机系统信息（包含主板信息）
        ComputerSystem computerSystem = hardware.getComputerSystem();
        // 从主板信息中获取型号
        String model = computerSystem.getBaseboard().getModel();

        // 处理空值或占位符
        if (model == null || model.trim().isEmpty() || "To Be Filled By O.E.M.".equals(model.trim()) || "Default String".equals(model.trim()) || "None".equals(model.trim())) {
            return null;
        }

        return model;
    }

    /**
     * 获取系统中所有网络接口的IP地址（包括IPv4和IPv6）
     */
    public static List<String> getAllIpAddresses() {
        List<String> ipAddresses = new ArrayList<>();

        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        List<NetworkIF> networkIFs = hardware.getNetworkIFs();

        for (NetworkIF networkIF : networkIFs) {
            // 处理IPv4地址（6.4.6版本通过getIPv4addr()获取）
            String[] ipv4s = networkIF.getIPv4addr();
            if (ipv4s != null) {
                for (String ipv4 : ipv4s) {
                    if (isValidIpv4(ipv4)) {
                        ipAddresses.add(ipv4);
                    }
                }
            }

            // 处理IPv6地址（6.4.6版本通过getIPv6addr()获取）
            String[] ipv6s = networkIF.getIPv6addr();
            if (ipv6s != null) {
                for (String ipv6 : ipv6s) {
                    if (isValidIpv6(ipv6)) {
                        ipAddresses.add(ipv6);
                    }
                }
            }
        }

        return ipAddresses;
    }

    /**
     * 验证IPv4地址有效性（非环回、非空）
     */
    private static boolean isValidIpv4(String ipv4) {
        if (ipv4 == null || ipv4.trim().isEmpty()) {
            return false;
        }
        // 排除环回地址（127.x.x.x）
        return !ipv4.startsWith("127.");
    }

    /**
     * 验证IPv6地址有效性（非环回、非临时地址）
     */
    private static boolean isValidIpv6(String ipv6) {
        if (ipv6 == null || ipv6.trim().isEmpty()) {
            return false;
        }
        // 排除环回地址（::1）和临时地址（包含%）
        return !"::1".equals(ipv6) && !ipv6.contains("%");
    }

    /**
     * 单独获取所有IPv4地址
     */
    public static List<String> getIpv4Addresses() {
        return getAllIpAddresses().stream().filter(ip -> ip.contains(".")) // IPv4包含点分符号
                .collect(Collectors.toList());
    }

    /**
     * 单独获取所有IPv6地址
     */
    public static List<String> getIpv6Addresses() {
        return getAllIpAddresses().stream().filter(ip -> ip.contains(":")) // IPv6包含冒号
                .collect(Collectors.toList());
    }
}
