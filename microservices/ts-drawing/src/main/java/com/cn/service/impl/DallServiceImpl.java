package com.cn.service.impl;

import com.cn.dto.DallTaskDto;
import com.cn.service.DallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DallServiceImpl implements DallService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDallDrawingTask(final DallTaskDto dto) {

    }


}
