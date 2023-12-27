package com.filesystem.filesystemweb.mapper;

import com.filesystem.filesystemweb.entity.Files;
import com.filesystem.filesystemweb.entity.UserFiles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Efar
* @description 针对表【user_files】的数据库操作Mapper
* @createDate 2023-12-28 02:01:26
* @Entity com.filesystem.filesystemweb.entity.UserFiles
*/
public interface UserFilesMapper extends BaseMapper<UserFiles> {

    /**
     * 根据用户ID获取文件列表
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




