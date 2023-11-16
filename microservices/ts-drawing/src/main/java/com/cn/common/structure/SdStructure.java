package com.cn.common.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class SdStructure implements Serializable {

    private String requestUrl;

    private List<SdModel> modelList;

    private List<Integer> stepsList;

    private List<String> samplerList;

    @Data
    @Accessors(chain = true)
    public static class SdModel {

        private String modelName;

        private String modelText;
    }

}
