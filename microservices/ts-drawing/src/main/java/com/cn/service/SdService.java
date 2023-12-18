package com.cn.service;

import com.cn.dto.SdTaskDto;
import com.cn.vo.SdParamVo;

public interface SdService {


    String addSdDrawingTask(final SdTaskDto dto);


    /**
     * Gets sd param.
     *
     * @return the sd param
     */
    SdParamVo getSdParam();

}
