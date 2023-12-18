package com.cn.service;

import com.cn.dto.DallTaskDto;
import com.cn.dto.DialogueImageDto;
import com.cn.vo.DialogueImageVo;

public interface DallService {


    DialogueImageVo dialogGenerationImg(final DialogueImageDto dto);


    String addDallTask(final DallTaskDto dto);
}
