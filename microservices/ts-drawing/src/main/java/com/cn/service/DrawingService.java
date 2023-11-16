package com.cn.service;

import com.cn.dto.DallTaskDto;
import com.cn.dto.SdTaskDto;
import com.cn.vo.SdParamVo;
import com.cn.vo.TaskResultVo;

public interface DrawingService {


    void addDallDrawingTask(final DallTaskDto dto);

    void addSdDrawingTask(final SdTaskDto dto);

    TaskResultVo getDrawingTask();

    SdParamVo getSdParam();

}
