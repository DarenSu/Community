package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
//import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.DiscussPost;
//import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

//import java.util.Date;
import java.util.Date;
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

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abcddd");
        loginTicket.setStatus(0);
        // 1000ms * 60 * 10 = 10分钟
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abcddd");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abcddd", 1);
        loginTicket = loginTicketMapper.selectByTicket("abcddd");
        System.out.println(loginTicket);
    }

}
