package com.dc.backend.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "wp_secretkey")
public class SecretKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "init_key")
    private String initKey;

    @Column(name = "create_time")
    private Date createAt;

    @Column(name = "update_time")
    private Date updateAt;

}
