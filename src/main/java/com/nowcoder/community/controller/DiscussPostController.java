package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;// 有用于获取当前用户

    @Autowired
    UserService userService;

    // 增加帖子的请求，异步请求   因为是增加数据，浏览器会提交很多数据，所以用POST
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {// 页面只需要传输需要过滤的内容就可以
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }
        // 构造一个实体出来
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    // 帖子详细信息展示
    @RequestMapping( path = "/detail/{discussPostId}", method = RequestMethod.GET) // model对象携带相关数据，主要是查询的结果
    public String getDiscussPost(@PathVariable("discussPostId")  int discussPostId, Model model){
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        //model存储查询的信息
        model.addAttribute("post", post);
        // 但是post里面存储的是用户的id，不是用户的名字，所以需要继续查询用户得到名字
        // 查询名字有两种方法，一种是在discusspost-mapper.xml中查询语句selectDiscussPostById中进行关联查询，
        //      虽然关联查询快点，但是这样子会使的这个查询语句不是很好复用，因为很多别的功能并不需要查询用户名
        // 所以使用仙茶道id,再在user表中查询用户名等信息
        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }

}
