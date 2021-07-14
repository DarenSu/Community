package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

    // 查询数据，使用ticket进行查询
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where user_id=#{userId} and status = 0"
    })
    LoginTicket findLoginTicketByUserId(int userId);
}
