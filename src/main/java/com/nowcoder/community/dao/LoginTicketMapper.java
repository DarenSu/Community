package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface LoginTicketMapper {

    // 插入数据    在mapper类中使用注解的方式进行，通过注解的方式区生命使用什么SQL
    // values里面是loginTicket里面的数据
    // 这种注解的方式简单，不需要写XML中的配置文件，但是要是语句复杂的话，可读性不高

    @Insert({
            "insert into login_ticket(userid,ticket,status,expired) ",
            "values(#(userId),#(ticket),#(status),#(expired))"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLogin(LoginTicket loginTicket);

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    // 查询数据，使用ticket进行查询
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    // 修改凭证的状态，你退出的时候凭证要失效，可以修改状态，也可以删除，我们使用状态修改，便于以后统计
    // 注解里面些SQL也支持动态的SQL，如if ，使用到 "<script>", "" "</script>"， 不方便阅读
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
