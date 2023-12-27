package com.filesystem.filesystemweb.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.filesystem.filesystemweb.entity.Files;
import com.filesystem.filesystemweb.entity.UserFiles;
import com.filesystem.filesystemweb.entity.Users;
import com.filesystem.filesystemweb.service.UserFilesService;
import com.filesystem.filesystemweb.service.UsersService;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 主页控制器
 * @Author Efar <efarxs@163.com>
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2023/12/28
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserFilesService userFilesService;
    private final UsersService usersService;


    /**
     * 注销登录
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public @ResponseBody Map<String, Object> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("code",200);
        resultMap.put("message","注销成功");

        return resultMap;
    }

    /**
     * 用户注册
     * @param username
     * @param password
     * @param captcha
     * @param token
     * @param request
     * @return
     */
    @PostMapping("/register")
    public @ResponseBody Map<String, Object> register(String username, String password, String captcha, String token, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>(2);
        Object verifyCode = request.getSession().getAttribute("captcha");
        if (verifyCode == null) {
            resultMap.put("code",-101);
            resultMap.put("message","验证码失效了，请刷新页面");
            return resultMap;
        }
        if (!verifyCode.toString().equalsIgnoreCase(captcha)) {
            resultMap.put("code",-102);
            resultMap.put("message","验证码校验失败");
            return resultMap;
        }
        Users user = usersService.getOne(Wrappers.<Users>lambdaQuery().eq(Users::getUsername, username));
        if (user != null) {
            resultMap.put("code",-100);
            resultMap.put("message","注册失败，用户名已被占用");
            return resultMap;
        }
        boolean vip = false;
        // 判断口令是否正确
        if (token != null && "我要VIP".equals(token)) {
            vip = true;
        }
        // 可以注册
        user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        user.setVip(vip);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        usersService.save(user);

        resultMap.put("code",200);
        resultMap.put("message","注册成功");
        return resultMap;
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public @ResponseBody Map<String,Object> login(String username, String password, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>(2);
        Users user = usersService.getOne(Wrappers.<Users>lambdaQuery().eq(Users::getUsername, username).eq(Users::getPassword, password));
        if (user == null) {
            resultMap.put("code",-100);
            resultMap.put("message","登录失败，账号或密码不匹配");

            return resultMap;
        }

        resultMap.put("code",200);
        resultMap.put("message","登录成功");

        // 保存到Session
        request.getSession().setAttribute("user",user);
        return resultMap;
    }

    /**
     * 验证码接口
     * @param request
     * @param response
     */
    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/gif");
        //生成验证码对象,三个参数分别是 宽、高、位数
        GifCaptcha captcha = new GifCaptcha(130, 48, 5);
        // 设置验证码的字符类型为字母
        captcha.setCharType(Captcha.TYPE_ONLY_CHAR);
        // 设置内置字体
        captcha.setCharType(Captcha.FONT_1);
        // 验证码存入session
        request.getSession().setAttribute("captcha",captcha.text().toLowerCase());
        try(ServletOutputStream out = response.getOutputStream()) {
            //输出图片流
            captcha.out(out);
        } catch (IOException ignored) {
        }
    }

    /**
     * 注册页面
     * @return
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 登录页面
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 文件管理页面
     * @return
     */
    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // 获取自己的文件记录
        Users user = (Users) request.getSession().getAttribute("user");
        List<Files> list = userFilesService.getFilesByUserId(user.getId());

        model.addAttribute("list",list);
        model.addAttribute("user",user);
        return "index";
    }
}
