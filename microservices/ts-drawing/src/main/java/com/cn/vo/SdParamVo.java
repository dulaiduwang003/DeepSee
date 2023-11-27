package com.cn.vo;

import com.cn.common.structure.SdStructure;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class SdParamVo implements Serializable {

    private List<SdStructure.SdModel> modelList;

    private List<String> samplerList;

    private SdStructure.Steps steps;


}
