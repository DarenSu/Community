package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 起到容器的作用，持有用户信息,用于代替session对象。
 */
@Component   // 注解声明下，交给Spring容器去管理
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();
    // 获取当前线程，并一起为key，进行存储
    public void setUser(User user) {
        users.set(user);
    }
    // 获取当前线程，并以其为key，获取值
    public User getUser() {
        return users.get();
    }
    // 获取当前线程，并将其清掉
    public void clear() {
        users.remove();
    }

}
