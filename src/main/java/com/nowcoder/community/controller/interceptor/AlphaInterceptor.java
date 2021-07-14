package com.nowcoder.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component // 注解声明下，交给Spring容器去管理
public class AlphaInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    // 在Controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // handler的输出为：com.nowcoder.community.controller.LoginController#getLoginPage()
        logger.debug("preHandle: " + handler.toString());
        return true;// 继续执行， 如果是false的话，就是不执行这个请求
    }

    // 在Controller之后执行, 主要的请求已经处理完了，下一步就是去模板引擎了，给页面返回要渲染的内容了
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // handler的输出为：com.nowcoder.community.controller.LoginController#getLoginPage()
        logger.debug("postHandle: " + handler.toString());
    }

    // 在TemplateEngine之后执行，即模板引擎之后执行的
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // handler的输出为：com.nowcoder.community.controller.LoginController#getLoginPage()
        logger.debug("afterCompletion: " + handler.toString());
    }
}
