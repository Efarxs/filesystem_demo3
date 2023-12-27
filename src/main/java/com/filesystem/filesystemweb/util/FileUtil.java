package com.filesystem.filesystemweb.util;

import org.springframework.stereotype.Component;

/**
 * @Description 文件工具
 * @Author Efar <efarxs@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2023/12/28
 */
@Component(value = "FileUtil")
public class FileUtil {
    /**
     * 格式化文件大小
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (size >= GB) {
            return String.format("%.2f GB", (double) size / GB);
        } else if (size >= MB) {
            return String.format("%.2f MB", (double) size / MB);
        } else if (size >= KB) {
            return String.format("%.2f KB", (double) size / KB);
        } else {
            return String.format("%d B", size);
        }
    }
}
