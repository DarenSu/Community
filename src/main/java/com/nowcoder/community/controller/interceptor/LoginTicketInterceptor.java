package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component   // 注解声明下，交给Spring容器去管理   ，另外也实现HandlerInterceptor接口
public class LoginTicketInterceptor implements HandlerInterceptor {

    /**
     * 按照之前话的逻辑，那么要在请求一开始就获得ticket，然后进行查询是否有这个用户
     * 为什么一开始就要做呢，因为我们在请求中随时随地都要用到当前用户，所以一开始就找到比较好
     */

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // cookie是从request里面传回来的，从request里面获取cookie，我们希望稍微封装一下，
        // 因为我们后面还有别的工具，也会有这个类似的情况，也会利用request获得cookie，从这个对象获取cookie还有点麻烦，所以封装下，一劳永逸
        // 以后也好复用

        // 从cookie中获取凭证  那么为ticket的cookie
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 查询凭证的整个对象
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效         不为空   状态为1（登录状态）  凭证的时间没有过期       超时时间在当前时间之后--有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 我们查到平整之后不是在这边使用，而是在后面用，在模板上用，或者是在我们controller处理业务的时候也可能会用
                // 也就是我们在后面的处理过程中个随时随地都会用，所以为了后面的使用，我需要将这个user暂存一下
                // 在本次请求中持有用户，用hostHolder持有user，相当于将数据存入当前线程对应的map里面，
                //      当这个请求没有处理完还在，这个线程就一直存在，当请求处理完，服务器对浏览器做出响应之后，这个线程被销毁
                // 所以说在整个请求处理过程中，线程一直是活着的，ThreadLocal里面的数据一直还在
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    // 在模板引擎调用之前要用到user，所以我们在模板之前将user存到modelAndView里面
    // 这个方法执行完之后，模板就会执行，模板执行的时候，modelAndView里面已经存储啦user，就可以拿来用了，就很方便
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    // 什么时候清理掉呢，那就是在模板调用结束之后清理掉
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }


}
