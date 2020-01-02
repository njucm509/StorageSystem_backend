package com.dc.backend.service.impl;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.mapper.UserMapper;
import com.dc.backend.pojo.UserPojo;
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
    public UserPojo login(UserPojo user) {
        return mapper.login(user);
    }

    @Override
    public void create(UserPojo user) {
        mapper.insertSelective(user);
    }

    @Override
    public void delete(UserPojo user) {
        mapper.delete(user);
    }

    @Override
    public PageResult<UserPojo> queryUserByPageAndSort(PageParam pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getRows());
        Example example = new Example(UserPojo.class);
        if (StringUtils.isNotBlank(pageParam.getKey())) {
            example.createCriteria().andLike("name", "%" + pageParam.getKey() + "%");
        }
        if (StringUtils.isNotBlank(pageParam.getSortBy())) {
            String orderByClause = pageParam.getSortBy() + (pageParam.getDesc() ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        Page<UserPojo> pageInfo = (Page<UserPojo>) mapper.selectByExample(example);
        log.info("pageInfo: {}", pageInfo);
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    @Override
    public List<UserPojo> queryAll() {
        return mapper.selectAll();
    }

    @Override
    public UserPojo queryById(Integer id) {
        return (UserPojo) mapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(UserPojo userPojo) {
        mapper.update(userPojo);
    }
}
