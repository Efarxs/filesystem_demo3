package com.filesystem.filesystemweb.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.filesystem.filesystemweb.config.properties.FileSystemProperties;
import com.filesystem.filesystemweb.entity.Files;
import com.filesystem.filesystemweb.entity.Users;
import com.filesystem.filesystemweb.service.FilesService;
import com.filesystem.filesystemweb.service.UserFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @Description 文件相关控制器
 * @Author Efar <efarxs@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2023/12/28
 */
@Controller
@RequestMapping("file")
@RequiredArgsConstructor
public class FileController {

    private final FilesService filesService;
    private final UserFilesService userFilesService;
    private final FileSystemProperties fileSystemProperties;

    /**
     * 下载文件 实现解压限速
     * @param id
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        // 判断文件是否存在
        Files fileObj = userFilesService.getFileById(id);
        if (fileObj == null) {
            // 文件不存在了
            userFilesService.removeById(id);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 文件原始路径
        String filePath = fileSystemProperties.getPath() + fileObj.getLocation();
        File file = new File(filePath + ".zip");
        if (!file.exists()) {
            // 删除文件记录
            userFilesService.removeById(id);
            // 删除文件记录
            filesService.removeById(fileObj.getId());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {

            String encodedFileName = URLEncoder.encode(fileObj.getName(), StandardCharsets.UTF_8.toString());
            encodedFileName = encodedFileName.replaceAll("\\+", "%20");

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            BufferedInputStream inputStream = new BufferedInputStream(java.nio.file.Files.newInputStream(file.toPath()));
            // 先解压
            ZipInputStream zipIn = new ZipInputStream(inputStream);
            // 解压文件
            zipIn.getNextEntry();


            // 创建一个临时字节输出流 用来保存解压文件流数据
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            // 设定每次最大读取流大小
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = zipIn.read(buffer)) != -1) {
                bao.write(buffer,0, bytesRead);
            }

            // 关闭流 关闭流的顺序不建议调换，先用哪个就最后关哪个
            zipIn.close();
            inputStream.close();

            // 输出
            byte[] byteArray = bao.toByteArray();
            // 判断是否VIP 如果是VIP那就全速下载，如果不是，那么就读取50*1024b大小
            int size = user.getVip() ? byteArray.length : 50 * 1024;
            // 因为我们是字节数组流，索引是从0开始的，就算不是数组，我们一个文件的字节都是从0 开始的
            int start = 0;
            // 这里获取size和字节数组长度之间的最小值，取最小的那个
            // 为什么这么做呢？因为如果说我们读取的长度大小比字节数组的长度还小，比如说我的文件是10kb，但是我要求的速度是50kb，那还限速个屁啊。
            int end = Math.min(size,byteArray.length);
            // 循环逐步输出数据流
            // 获取输出流
            ServletOutputStream outputStream = response.getOutputStream();
            while (start < byteArray.length) {
                // 将当前块的数据写入response的输出流
                outputStream.write(byteArray, start, end - start);
                // 刷新缓冲区
                outputStream.flush();
                // 如果不是vip，那么就延迟1秒读取一次
                if (!user.getVip()) {
                    Thread.sleep(1000);
                }
                // 更新起始位置和结束位置 因为我们读取到了某一个位置的时候，下一次读取时，就要从这个位置开始
                start = end;
                // 这里的含义跟上面的end一样
                end = Math.min(start + size, byteArray.length);
            }

            outputStream.close();
            bao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public @ResponseBody Map<String,Object> delete(HttpServletRequest request, @PathVariable Integer id) {
        boolean b = userFilesService.removeById(id);
        Map<String,Object> resultMap = new HashMap<>(2);
        if (b) {
            resultMap.put("code",200);
            resultMap.put("message","文件删除成功");
        } else {
            resultMap.put("code",-100);
            resultMap.put("message","文件删除失败");
        }
        return resultMap;
    }

    /**
     * 检查hash是否存在
     * @param hash
     * @return
     */
    @RequestMapping("/check")
    public @ResponseBody Map<String,Object> checkHash(String hash, HttpServletRequest request) {
        Map<String,Object> resultMap = new HashMap<>(2);
        try {
            // 判断hash是否存在了
            Files files = filesService.getOne(Wrappers.<Files>lambdaQuery().eq(Files::getHash, hash));
            if (files != null) {

                // 添加到记录表
                Users user = (Users) request.getSession().getAttribute("user");
                userFilesService.addFile(user,files);

                resultMap.put("code",200);
                resultMap.put("message","文件上传成功");
            } else {
                resultMap.put("code",201);
                resultMap.put("message","checked");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            resultMap.put("code",-100);
            resultMap.put("message","上传失败：" + e.getMessage());
        }

        return resultMap;
    }

    /**
     * 文件上传
     * @return
     */
    @PostMapping("/upload")
    public @ResponseBody Map<String,Object> upload(@RequestParam("file") MultipartFile file, @RequestParam("md5") String hash, HttpServletRequest request) throws Exception {

        Map<String,Object> resultMap = new HashMap<>(2);
        Users user = (Users) request.getSession().getAttribute("user");
        try {
            // 判断hash是否存在了
            Files files = filesService.getOne(Wrappers.<Files>lambdaQuery().eq(Files::getHash, hash));
            if (files == null) {
                // 不存在，那么就新添加一个
                String location = "efar" + "/";
                String path = System.getProperty("user.dir") + File.separator + fileSystemProperties.getPath() + location;

                File dest = new File(path);
                // 如果目录不存在，那么就创建一个
                if(!dest.exists()){
                    dest.mkdirs();
                }

                // 保存文件

                // 压缩文件名
                String zipFileName = file.getOriginalFilename() + ".zip";

                // 创建压缩文件
                FileOutputStream fos = new FileOutputStream(path + zipFileName);
                ZipOutputStream zipOut = new ZipOutputStream(fos);

                // 添加文件到压缩文件
                ZipEntry zipEntry = new ZipEntry(file.getOriginalFilename());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(file.getBytes());
                zipOut.closeEntry();

                // 关闭流
                zipOut.close();
                fos.close();


                files = new Files();
                files.setSize(file.getSize());
                files.setName(file.getOriginalFilename());
                files.setHash(hash);
                files.setUserId(user.getId());
                files.setLocation(location + file.getOriginalFilename());
                files.setCreateTime(new Date());

                filesService.save(files);

            }
            // 添加到记录表
            userFilesService.addFile(user,files);
            resultMap.put("code",200);
            resultMap.put("message","上传成功" + hash);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("code",-100);
            resultMap.put("message","上传失败：" + e.getMessage());
        }

        return resultMap;
    }
}
