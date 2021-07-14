package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
//import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration   // 注解表示这是一个配置类
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 通常的配置类主要是想在里面声明一个第三方的bean ，通常是这样的情况；
     * 但是拦截器的逻辑和一般不一样，他要求实现一个接口，而不是简单装成一个bean，实现的是WebMvcConfigurer这个接口
     */

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    // 实现的方法，实现的时候spring都会将registry注入进来
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 这个拦截器使用.addInterceptor(alphaInterceptor)会拦截一切请求，
        // 要是不想拦截的话，可以使用.excludePathPatterns排除一些路径不拦截，静态资源没必要拦截，里面没有逻辑，
        // 若是想要拦截具体的请求呢，那就使用.addPathPatterns("/register", "/login");
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/register", "/login");

        // 静态资源不用处理，静态资源之外的资源，全部都要处理
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        // 静态资源不用处理，静态资源之外的资源，全部都要处理
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
