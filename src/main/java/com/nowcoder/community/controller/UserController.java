package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 处理与用户直接有关的逻辑
 * 访问路径：/user
 */

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    // 更新的是当前用户的头像，当前用户在哪，需要从hostHolder里面找，所以需要注入进来
    @Autowired
    private HostHolder hostHolder;


    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    // 上传的时候表单的提交必须是POST，这是要求
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        // 获取文件的名字，
        String fileName = headerImage.getOriginalFilename();
        // 并且截取其后缀名字,从"."后面截取
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        // 如果路径不存在，就新建路径
        if (!dest.getAbsoluteFile().exists()){
            dest.getParentFile().mkdirs();
        }

        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 上传成功了，就需要更新当前用户的头像的路径，更新的不是本地磁盘的路径（你自己的磁盘别人访问不到），而是web访问路径(web访问路径)
        // 访问路径：http://localhost:8080/community/user/header/xxx.png 访问路径需要下面写一个函数进行实现
        User user = hostHolder.getUser();
        System.out.println(user.getHeaderUrl());
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        System.out.println(user.getHeaderUrl());

        return "redirect:/index";
    }
    // 获取头像  不用加登录状态保持注解，因为不登录你也可以看别人的头像
    // 图片的web访问路径的方法实现     ，由于我们向浏览器相应的是一个特殊的东西，所以需要用到response
    // 由于向浏览器输出的不是一个字符或者网页，而是特殊的东西，一个图片，需要自己用response进行手动的输出，
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径， 在uploadHeader可以找到
        fileName = uploadPath + "/" + fileName;
        // 文件后缀  - 输出的什么要声明，主要是声明文件的格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片 ，这里从本地读取图片文件，并将其输出到浏览器。
        response.setContentType("image/" + suffix);
        try (// 图片是二进制数据，需要用到字节流
                // 创建文件输入流
                // java7 语法，这里的代码块生成的变量在定义的时候会自动加上final，在执行结束后可以自动关闭，前提是有关闭语法
                FileInputStream fis = new FileInputStream(fileName);// 得到输入流，由于自己创建的，不会主动关闭，需要手动关闭
                OutputStream os = response.getOutputStream();// 获取字节输出流，由SpringMVC创建，会自动关闭
             // 有了输入流之后，就开始输出了，输出的时候不要一个字节一个字节输出，要建立一个缓冲区，如一次最多输出1024字节，一批一批输出
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码，自己开发
     * 主要思路：
     *      1、修改不成功
     *              首先判断是否登陆成功，没有登陆的话就不显示这个修改密码的页面
     *            修改不成功的话，先寻找原因
     *              1. 旧密码不匹配（使用hostHolder.getUser()获取当前用户的密码，进行比对）
     *              2. 新密码两次的输入不一致（前端判断）
     *              3. 旧密码的格式不对，或新密码格式不对
     *      2、修改成功
     *            修改成功的话，需要返回到登录页面进行重新登录
     */
    @LoginRequired
    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, String repeatPassword, Model model,HttpServletResponse response){
        // 密码格式判断，新旧密码都需要判断
        if( StringUtils.length(newPassword)< PASSWORD_MIN_LENGTH||StringUtils.length(oldPassword)< PASSWORD_MIN_LENGTH||StringUtils.length(repeatPassword)< PASSWORD_MIN_LENGTH ){
            System.out.println("密码小于8位");
            model.addAttribute("formalError","密码格式错误，少于8位");
            return "/site/setting";
        }
        // 是否登陆成功
        User user = hostHolder.getUser();
        System.out.println("user="+ user);
        if (user == null ){
            System.out.println("未登录用户，请先登录");
            model.addAttribute("NoLogin","未登录用户，请先登录");
            return "/site/login";
        }
        // 重新设置的密码的两次输入对比，其实在前端可以做的
        if (!newPassword.equals(repeatPassword)){
            System.out.println("两次密码不一致，请重新输入！");
            model.addAttribute("twopasswordError","两次密码不一致，请重新输入！");
            return "/site/setting";
        }
        // 先对新密码进行加密
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        // 旧密码是否正确
//        System.out.println(user.getPassword() + "  " + oldPassword);
        if (!userService.findUserById(user.getId()).getPassword().equals(oldPassword)){
            model.addAttribute("oldpasswordError","旧密码输入错误，请重新输入！");
            return "/site/setting";
        }
        // 开始进行密码修改
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);
        //修改成功后需要重新登录，所以调用下面函数
        LoginTicket loginTicket = userService.findLoginTicketByUserId(user.getId());
        System.out.println("修改密码中的loginTicket:"+loginTicket);
        userService.logout(loginTicket.getTicket());
        System.out.println("修改密码中的loginTicket.getTicket():"+loginTicket.getTicket());
        return "redirect:/login"; // 重定向到登录页面，默认是get请求  -- 测试   看起来更明显
//        return "/site/login";// 修改成功后进行重新登录
    }


}
