package com.nowcoder.community;


import org.junit.Test;
//import org.junit.platform.commons.logging.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {
    //private-单独为每个类实例化一个，不同类使用不同的logger  static-静态的，每个地方都能用   final-不可改变
    private static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);


    @Test
    public void testLogger() {
        System.out.println(logger.getClass().getName());

        // 级别有低到高，根据application里面的，填写那个，就只能输出该级别及其之上的log
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");

    }
}
