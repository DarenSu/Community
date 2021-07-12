package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;
    // 发邮件的时候的几个条件
    // 1、发邮件的是谁
    // 2、谁来接受
    // 3、邮件的标题和内容
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content){
        // 这里面实现逻辑     给谁    来自谁   内容
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);//发件人
            helper.setTo(to);//收件人
            helper.setSubject(subject);
            helper.setText(content, true);//内容，  true代表其支持HTML文本，不加的话只是普通文本
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
