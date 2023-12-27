package com.filesystem.filesystemweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.filesystem.filesystemweb.entity.Users;
import com.filesystem.filesystemweb.service.UsersService;
import com.filesystem.filesystemweb.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author Efar
* @description 针对表【users】的数据库操作Service实现
* @createDate 2023-12-28 02:01:26
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




