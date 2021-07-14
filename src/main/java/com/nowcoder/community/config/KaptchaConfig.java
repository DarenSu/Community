package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean  // 这个bean被spring容器所管理，所装备
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");// 长
        properties.setProperty("kaptcha.image.height", "40");// 宽
        properties.setProperty("kaptcha.textproducer.font.size", "32");// 字的大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");// 颜色：   红绿蓝
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYAZ");// 随机字符的选择范围
        properties.setProperty("kaptcha.textproducer.char.length", "4");// 随机字符的长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");// 添加干扰，如线条、点  由于默认防破解，所以干扰加不加意义不大

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}
