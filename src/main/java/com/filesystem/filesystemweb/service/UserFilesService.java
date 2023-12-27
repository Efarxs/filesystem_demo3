package com.filesystem.filesystemweb.service;

import com.filesystem.filesystemweb.entity.Files;
import com.filesystem.filesystemweb.entity.UserFiles;
import com.baomidou.mybatisplus.extension.service.IService;
import com.filesystem.filesystemweb.entity.Users;

import java.util.List;

/**
* @author Efar
* @description 针对表【user_files】的数据库操作Service
* @createDate 2023-12-28 02:01:26
*/
public interface UserFilesService extends IService<UserFiles> {

    /**
     * 添加文件到用户文件表
     * @param user
     * @param file
     */
    void addFile(Users user, Files file);

    /**
     * 根据文件ID获取文件列表
     * @param id
     * @return
     */
    List<Files> getFilesByUserId(Integer id);

    /**
     * 根据ID获取文件
     * @param id
     * @return
     */
    Files getFileById(Integer id);
}
