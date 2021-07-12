package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.sun.javafx.collections.MappingChange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;
    // 注册的过程中需要发邮件  所以需要将邮件客户端和模板引擎注册进来
    @Autowired // 客户端
    private MailClient mailClient;
    @Autowired // 模板引擎
    private TemplateEngine templateEngine;

    // 还有发邮件的时候需要激活码，激活码中包含域名和项目名，所以需要将他两注释进来
    // 由于注入的是固定的值，不是bean，所以使用Value
    @Value("${community.path.domain}")// 域名
    private String domain;  // 生成字符串接收值
    @Value("${server.servlet.context-path}")// 项目名
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    // 注册的业务,写一个公有的方法，便于别人使用
    // 这个方法要返回一个结果，返回一个整数代表不同状态可以，返回一个集合包含很多信息也可以，返回一个自定义的类都可以
    // 实际上返回内容包含，账号为空，已存在，密码为空等等,所以使用map
    public Map<String, Object> register (User user){
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if( user == null ){
            try {
                throw new IllegalAccessException("参数不能为空!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 账号空值判断
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        // 密码空值判断
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        // 邮箱空值判断
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        // 验证账号 - 传入username看数据库中跟是否有
        User u = userMapper.selectByName(user.getUsername());
        if ( u != null ){
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if ( u != null ){
            map.put("mailMsg", "该邮箱已被注册！");
            return map;
        }
        // 注册就是将用户信息存到库里
        // 账号密码邮箱不为空，并且账号邮箱不存在，下面就可以进行注册了
        // 保存之前还得对密码进行加密，密码加密是先加上salt，在进行加密的
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));// 只取五位
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 然后其他的字段还需要重新设置下,差UN寄哪里啊的只有邮箱账号密码
        user.setType(0);// 类型-注册用户默认为普通用户
        user.setStatus(0);// 状态都是0，表示没有激活
        user.setActivationCode(CommunityUtil.generateUUID());// 激活码,可以长一点
        // 下面是随机头像图片，牛客网友一千个随即头像，网址为：https://images.nowcoder.com/head/1t.png,其中1可以改变0~1000
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        // 调用insert之后，mybatis会自动获取id，并且回填
        userMapper.insertUser(user);// insert之后，mybatis会将最新值回填给user的，具体配置在application里面

        // 发送邮件,给用户一个激活邮件, 发送的是HTML文件，方便连接
        Context context = new Context();
        context.setVariable("username", user.getEmail());
        // 激活连接  https://localhost:8081/community/activation/id/激活码
        System.out.println("contextPath:"+contextPath);
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        System.out.println("url="+url);
        context.setVariable("url", url);
        // 下面是生成邮件的内容，利用模板引擎
        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号",content);


        return map;
    }

    // 激活的行为 , 看下上面的激活连接，主要传入两个参数  id 激活码
    public int activation(int userId, String code){
        /*
        激活有三种：成功激活，重复激活，激活失败
         */
        User user = userMapper.selectById(userId);
        if( user.getStatus() == 1){ // 重复激活
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) { // 没有激活可以激活
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else { // 激活失败
            return ACTIVATION_FAILURE;
        }
    }

}
