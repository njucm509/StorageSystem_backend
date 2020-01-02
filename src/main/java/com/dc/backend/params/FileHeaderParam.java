package com.dc.backend.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileHeaderParam {
    private Integer defaultEnc;
    private String encryption;
    private String content;
}
