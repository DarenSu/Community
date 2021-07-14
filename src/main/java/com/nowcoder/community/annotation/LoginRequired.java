package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 作用在方法上，对方法有作用
@Retention(RetentionPolicy.RUNTIME) // 程序运行时有效
public @interface LoginRequired {

    /**
     * 里面不用写内容
     * 这个自定义注解主要用于需要进行登录状态判断的方法，在需要的方法上面加上这个注解就可以
     */

}
