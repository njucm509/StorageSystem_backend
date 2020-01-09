package com.dc.backend.params;

import com.dc.backend.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileParam implements Serializable {
    private String filename;
    private List<FileHeaderParam> list;
    private User user;
}
