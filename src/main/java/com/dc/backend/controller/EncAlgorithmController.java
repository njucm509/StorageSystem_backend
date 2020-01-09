package com.dc.backend.controller;

import com.dc.backend.entity.PageParam;
import com.dc.backend.entity.PageResult;
import com.dc.backend.pojo.EncAlgorithm;
import com.dc.backend.service.EncAlgorithmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class EncAlgorithmController {

    @Autowired
    private EncAlgorithmService service;

    @RequestMapping("/encalgorithm/page")
    public ResponseEntity<PageResult<EncAlgorithm>> queryEncRecordByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                      @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                                                      @RequestParam(value = "sortBy", required = false) String sortBy,
                                                                      @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                                                      @RequestParam(value = "key", required = false) String key) {
        PageParam pageParam = PageParam.builder().page(page).rows(rows).sortBy(sortBy).desc(desc).key(key).build();
        log.info("pageParam: {}", pageParam);
        PageResult<EncAlgorithm> result = service.queryUserByPageAndSort(pageParam);
        if (result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping("/encalgorithm/update")
    public void updateStatus(@RequestParam("id") Integer id, @RequestParam("status") Integer status) {
        log.info("update status: {}---{}", id, status);
        EncAlgorithm encAlgorithm = new EncAlgorithm();
        encAlgorithm.setId(id);
        encAlgorithm.setStatus(status);
        encAlgorithm.setUpdateAt(new Date());
        service.updateStatus(encAlgorithm);
    }
}
