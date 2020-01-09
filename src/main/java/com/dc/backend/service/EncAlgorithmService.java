package com.dc.backend.service;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.mapper.EncAlgorithmMapper;
import com.dc.backend.pojo.EncAlgorithm;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Slf4j
@Service
public class EncAlgorithmService {

    @Autowired
    private EncAlgorithmMapper mapper;

    public PageResult<EncAlgorithm> queryUserByPageAndSort(PageParam pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getRows());
        Example example = new Example(EncAlgorithm.class);
        if (StringUtils.isNotBlank(pageParam.getKey())) {
//            example.createCriteria().andLike("name", "%" + pageParam.getKey() + "%");
        }
        if (StringUtils.isNotBlank(pageParam.getSortBy())) {
            String orderByClause = pageParam.getSortBy() + (pageParam.getDesc() ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        Page<EncAlgorithm> pageInfo = (Page<EncAlgorithm>) mapper.selectByExample(example);
        log.info("pageInfo: {}", pageInfo);
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    public void updateStatus(EncAlgorithm encAlgorithm) {
        int i = mapper.updateByPrimaryKeySelective(encAlgorithm);
    }
}
