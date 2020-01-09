package com.dc.backend.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "wp_encrecord")
public class EncRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String filename;

    private String record;

    @Column(name = "create_time")
    private Date createAt;

}
