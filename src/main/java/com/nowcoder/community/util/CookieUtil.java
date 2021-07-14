package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    //  参数说明：  首先你的要传入 request，   还有你得告诉我你要去的那个对象的名字是什么name
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }
        // 得到所有的cookie，放进数组里
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 处理json字符串的方法，我们平时开发更倾向于使用fasfjson，比spring的更高效

}
