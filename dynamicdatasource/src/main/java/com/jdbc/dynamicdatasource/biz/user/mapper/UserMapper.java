package com.jdbc.dynamicdatasource.biz.user.mapper;

import com.jdbc.dynamicdatasource.annotation.DataSource;
import com.jdbc.dynamicdatasource.constant.DataSourceType;
import com.jdbc.dynamicdatasource.entity.User;

import java.util.List;

/**
 * @Description: UserMapper接口
 */
@DataSource(DataSourceType.SLAVE)
public interface UserMapper {

    /**
     * 新增用户
     * @param user
     * @return
     */
    @DataSource  //默认数据源
    int save(User user);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @DataSource(DataSourceType.SLAVE)  //默认数据源
    int update(User user);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DataSource  //默认数据源
    int deleteById(Long id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @DataSource(DataSourceType.SLAVE)  //slave1
    User selectById(Long id);

    /**
     * 查询所有用户信息
     * @return
     */
    List<User> selectAll();
}
