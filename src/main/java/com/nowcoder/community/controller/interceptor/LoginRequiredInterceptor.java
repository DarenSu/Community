package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;
    /**
     * 驳回非登录用户对于 需要登录状态的请求
     * @param request
     * @param response
     * @param handler - handler是拦截的目标，要根据其判断是否是方法，因为我们值拦截方法，要是方法的话那就是HandlerMethod，默认指定的
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检验拦截到是不是一个方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 检查拦截的方法是否被LoginRequired注释
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired != null && hostHolder.getUser() == null) {// 当前方法被拦截了，但是没有登陆
                // 由于需要返回boolean的false，所以不能使用return "redirect:/login";进行重定向，
                // 从而改用response进行重定向
                // 在没有登录保持的情况下，访问了不允许访问的页面，被拦截下来，并且强制其直接返回到首页
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
