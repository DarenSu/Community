package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface DiscussPostMapper {

    // 搜索所有的数据   后面两个参数是因为考虑到分页了，     起始行的行号  最多显示多少数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    // 既然考虑到分页了，那么就需要知道分多少页，   分多少页=一共多少条数据/每页多少条数据
    // 所以为了显示方便，还需要知道一共有多少数据，方便分页
    // @Param注解用于给参数取别名,作用一：参数名字很长的话，起这个别名可以不用在SQL里面继续使用这么长的名字
    // 如果只有一个参数，并且在<if>使用，则必须取别名
    int selectDiscussPostRows(@Param("userId") int userId);


}


