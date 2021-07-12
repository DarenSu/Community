package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    // thymeleaf模板引擎里面有一个核心的类，被spring管理起来，可以直接用
    private TemplateEngine templateEngine;

    @Test
    // 发送简单的文本模板
    public void testTextMail(){
//        mailClient.sendMail("2929016824@qq.com", "Test For Mail", "Welcome.");
        //                      发往的邮箱地址                 标题          内容
        mailClient.sendMail("18821787628@163.com", "TEST", "Welcome.");
    }

    // MVC的话，在controller里面的话直接返回模板的路径自动就掉了就行，
    // 这里发邮件测试的地方不能那样做，需要主动调用才行
    // thymeleaf模板引擎里面有一个核心的类，被spring管理起来，可以直接用,然后才能自动调用
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        // 将需要传到模板的变量存到context对象里面
        //                          参数名            参数值
        context.setVariable("username", "sunday");

        // 参数构建玩，调用模板引擎生成HTML文件             模板文件存储位置   数据
        String content = templateEngine.process("/mail/demo", context);// 生成一个动态网页，其实就是一个string
        System.out.println(content);

        String mail1 = "18821787628@163.com";
        mailClient.sendMail(mail1, "HTML", content);
    }
}


