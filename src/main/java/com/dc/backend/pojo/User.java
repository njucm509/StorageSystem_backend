package com.dc.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wp_user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "account")
    private String account;

    @Column(name = "password")
    private String pwd;

    // 1 管理员  0 普通用户
    @Column(name = "role")
    private Integer role;

    @Column(name = "create_time")
    private Date createAt;

    @Column(name = "update_time")
    private Date updateAt;
}
