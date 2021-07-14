package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    // 敏感词过滤注解
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    // 增加帖子，由于发帖子的时候使用的大段大段的文字，所以我们需要使用过滤器对单词进行过滤
    // 在里面追加一个方法就可以了
    public int addDiscussPost(DiscussPost post){
        if( post == null ){
            throw new IllegalArgumentException("参数不能为空");
        }
        // 对post里面的数据进行敏感词过滤
        // 很明显 id和数字不需要，需要进行过滤的只有 title和content
        // 另外，除了敏感词之外，一些标签也需要去掉，因为<script>xcs<script>在网页上展示的时候，对页面有损伤破坏，
        // 只想让网页将所有内容当作文字而不是标签之类的

        // 先转移HTML标记，会将里面带有大于号小于号的字符转成转译字符
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 转移之后就需要进行敏感词处理
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        // 实现插入数据
        return discussPostMapper.insertDiscussPost(post);

    }

    // 帖子详细信息展示
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }


}
