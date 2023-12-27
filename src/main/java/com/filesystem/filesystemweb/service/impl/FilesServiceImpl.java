package com.filesystem.filesystemweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.filesystem.filesystemweb.entity.Files;
import com.filesystem.filesystemweb.service.FilesService;
import com.filesystem.filesystemweb.mapper.FilesMapper;
import org.springframework.stereotype.Service;

/**
* @author Efar
* @description 针对表【files】的数据库操作Service实现
* @createDate 2023-12-28 02:01:26
*/
@Service
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files>
    implements FilesService{

}




