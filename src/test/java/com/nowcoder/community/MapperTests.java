package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
//import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
//import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

//import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    /**
     *
     */
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectPosts(){
        System.out.println("进入该测试函数！");

        int rows= discussPostMapper.selectDiscussPostRows(101);
        System.out.println("rows="+rows);

        List<DiscussPost> list=  discussPostMapper.selectDiscussPosts(101,0,10);
        for ( DiscussPost post : list) {
            System.out.println(post);
        }



//        int rows= discussPostMapper.selectDiscussPostRows(0);
//        System.out.println(rows);
    }
}
