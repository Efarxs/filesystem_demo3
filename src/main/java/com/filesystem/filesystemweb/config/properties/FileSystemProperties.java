package com.filesystem.filesystemweb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description 配置文件自动读取
 * @Author Efar <efarxs@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2023/12/28
 */
@Component
@Data
@ConfigurationProperties(prefix = "filesystem")
public class FileSystemProperties {
    private String path;
}
