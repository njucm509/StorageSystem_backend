package com.dc.backend.service.impl;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.mapper.UserMapper;
import com.dc.backend.pojo.User;
import com.dc.backend.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper mapper;

    @Override
    public User login(User user) {
        return mapper.login(user);
    }

    @Override
    public void create(User user) {
        mapper.insertSelective(user);
    }

    @Override
    public void delete(User user) {
        mapper.delete(user);
    }

    @Override
    public PageResult<User> queryUserByPageAndSort(PageParam pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getRows());
        Example example = new Example(User.class);
        if (StringUtils.isNotBlank(pageParam.getKey())) {
            example.createCriteria().andLike("name", "%" + pageParam.getKey() + "%");
        }
        if (StringUtils.isNotBlank(pageParam.getSortBy())) {
            String orderByClause = pageParam.getSortBy() + (pageParam.getDesc() ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        Page<User> pageInfo = (Page<User>) mapper.selectByExample(example);
        log.info("pageInfo: {}", pageInfo);
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    @Override
    public List<User> queryAll() {
        return mapper.selectAll();
    }

    @Override
    public User queryById(Integer id) {
        return (User) mapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(User user) {
        mapper.update(user);
    }
}
