package com.filesystem.filesystemweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.filesystem.filesystemweb.entity.Files;
import com.filesystem.filesystemweb.entity.UserFiles;
import com.filesystem.filesystemweb.entity.Users;
import com.filesystem.filesystemweb.service.UserFilesService;
import com.filesystem.filesystemweb.mapper.UserFilesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Efar
* @description 针对表【user_files】的数据库操作Service实现
* @createDate 2023-12-28 02:01:26
*/
@Service
@RequiredArgsConstructor
public class UserFilesServiceImpl extends ServiceImpl<UserFilesMapper, UserFiles>
    implements UserFilesService{

    private final UserFilesMapper userFilesMapper;

    /**
     * 根据ID获取文件
     * @param id
     * @return
     */
    @Override
    public Files getFileById(Integer id) {
        return userFilesMapper.getFileById(id);
    }

    /**
     * 根据用户ID 获取文件列表
     * @param id
     * @return
     */
    @Override
    public List<Files> getFilesByUserId(Integer id) {
        return userFilesMapper.getFilesByUserId(id);
    }

    /**
     * 添加文件记录到用户文件记录表
     * @param user
     * @param file
     */
    @Override
    public void addFile(Users user, Files file) {
        // 判断这个记录是否已经存在了
        LambdaQueryWrapper<UserFiles> lq = new LambdaQueryWrapper<>();
        lq.eq(UserFiles::getUserId,user.getId())
                .eq(UserFiles::getFileId,file.getId());
        UserFiles one = this.getOne(lq);
        if (one != null) {
            throw new RuntimeException("文件记录已经存在了，无法上传");
        }

        // 可以添加到记录
        UserFiles userFiles = new UserFiles();
        userFiles.setUserId(user.getId());
        userFiles.setFileId(file.getId());
        userFiles.setCreateTime(new Date());

        this.save(userFiles);
    }
}




