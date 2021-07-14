package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    // 都是简单的静态方法，所以直接掉就行，不用容器托管
    // 并且还需要生成随机字符串，即激活码功能，在一个将来还需要做上传头像的功能，或者是上传文件的功能，上传后还需要生成一个随机的字符串进行命名

    // 生成随机字符串
    public static String generateUUID() {
        //                                          将所有的横线替换成空字符串
        return UUID.randomUUID().toString().replace("-","");
    }

    // MD5加密
    // 只能加密不能解密，   hello -> abc123def456   每次加密的结果都是这个值   所以前面的密码简单了，后面人家可以暴力破解，或者自己生成对应的库
    // 因此，我们一般都是价格随机字符串，没有规律的
    // hello + 3e4a8  -> abc123def456abc   随机字符串是随机的，黑客数据库里面没有这个记录，并且随机字符串越长破解难度越大，加上中文后更难破解
    public static String md5(String key){
        if(StringUtils.isAllBlank(key)){// 判断是null、空字符串、空格都是空的
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    // 方法：   一般服务器想浏览器返回的json数据往往包含三块内容，我们将这三块内容正和岛一起
    //      参数1、向浏览器返回编码，编号，0代表什么，1代表什么
    //      参数2、向浏览器返回提示信息，提示成功失败
    //      参数3、向浏览器返回业务数据，业务数据不同功能不一样
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
    // 下面是重载，因为有些地方不一定有三个参数，可能只有一个参数或者两个
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
    // 测试上面的json方法
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }


}
