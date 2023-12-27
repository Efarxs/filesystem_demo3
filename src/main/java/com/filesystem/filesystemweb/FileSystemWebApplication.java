package com.filesystem.filesystemweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.filesystem.filesystemweb.mapper"})
public class FileSystemWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileSystemWebApplication.class, args);
    }

}
