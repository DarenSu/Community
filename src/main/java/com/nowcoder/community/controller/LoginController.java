package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.apache.commons.lang3.StringUtils;
import com.sun.xml.bind.api.ErrorListener;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
        System.out.println("注册的页面的user："+user);
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

    // 获取验证码图片
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    // 由于向浏览器输出的不是一个字符或者网页，而是特殊的东西，一个图片，需要自己用response进行手动的输出，所以需要使用void
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码  之后服务器要记住，并且还不能存储在浏览器端，要不然很容易被盗取，验证码是敏感信息
        // 存到服务器端，要在多个请求之间用，我生成存进去，然后登陆的时候再用，所以说要跨请求的
        // 所以我们就可以利用cookie或session，但是由于是敏感数据，所以使用session比较合适

        // 生成一个四位的字符串
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);// 生成对应的图片

        // 将验证码存入session
        session.setAttribute("kaptcha", text);

        // 将突图片输出给浏览器
        response.setContentType("image/png");
        // response向浏览器做响应，我们需要获取器输出流，  整个response是springMCV维护的，自动会关闭，所以下面的os也不用手动关闭
        try {
            OutputStream os = response.getOutputStream();// 图片用这个比较好
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }
    // 两个login可以的，但是后面的method必须不同才可以
    // 参数说明  rememberme-是否记住我   model-返回数据时作为提示使用  session-输入的验证码要和我之前存储的比较，而验证码放在session里面
    // response-要是登录成功了，还要发放cookie，cookie便是放到response里面发给客户端的
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, HttpSession session, HttpServletResponse response) {
        System.out.println("登录界面的密码password："+password);

        // 检查验证码  验证码要是不对，其他的也就没必要看了
        String kaptcha = (String) session.getAttribute("kaptcha");
        // 两个验证码不能为空，并且还要相等
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 检查账号,密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // 成功才会在map里面方ticket,否则都是失败的
        if (map.containsKey("ticket")) {// 成功重定向到首页之前需要先将ticket发送给客户端让其存储
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath); // cookie的有效路径，应该是整个项目内都是有效的
            cookie.setMaxAge(expiredSeconds);// 有效时间
            response.addCookie(cookie);// 发送给页面
            return "redirect:/index"; // 成功的话重定向到首页
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login"; // 不成功的话跳转到登录页
        }
    }
    // 退出登录页请求
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login"; // 重定向到登录页面，默认是get请求  -- 测试   看起来更明显

//        return "redirect:/index"; // 重定向到首页，默认是get请求
//        return "/site/index";
    }
}
