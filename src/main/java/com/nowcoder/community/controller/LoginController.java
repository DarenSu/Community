package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    UserService userService;

    // 获取注册页面
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage () {
        return "/site/login";
    }

    // 获取登录页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage () {
        return "/site/register";
    }


    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        System.out.println("LoginController"+user);
        Map<String, Object> map = new HashMap<>();
        map = userService.register(user);
        if (map == null || map.isEmpty()){// map里面没有值，或者有值但是是空值，表明注册成功了
            // 注册成功了，就要进行跳转，由于还需要进行激活，所以先跳转到首页，而不是登录页面
            // 激活后跳转到登录页面
            // 注册成功后弹出的提示，直接在首页有点不好看，所以采用第三方页面 operate-result.html 即操作结果页面
            model.addAttribute("msg","注册成功，我们已向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");// 目标的设置，跳转到首页
            return "/site/operate-result";
        } else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 激活连接  https://localhost:8081/community/activation/id/激活码
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) { // 成功了  跳转登录页
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) { // 重复了  跳转首页
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else { // 失败了  跳转首页
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


}
