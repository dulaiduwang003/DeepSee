package com.cn.service;

import com.cn.dto.SdTaskDto;
import com.cn.vo.SdParamVo;
import com.cn.vo.TaskResultVo;

public interface SdService {


    String addSdDrawingTask(final SdTaskDto dto);


    SdParamVo getSdParam();

    TaskResultVo getDrawingTask(String taskId);
}
