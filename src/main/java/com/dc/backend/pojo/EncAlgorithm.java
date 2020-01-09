package com.dc.backend.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "wp_encalgorithm")
public class EncAlgorithm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    // 1 启用 0 禁用  默认启用
    private Integer status;

    // 是否能被删除 1 能 0 不能 默认能
    @Column(name = "canDelete")
    private Integer canDelete;

    @Column(name = "create_time")
    private Date createAt;

    @Column(name = "update_time")
    private Date updateAt;
}
