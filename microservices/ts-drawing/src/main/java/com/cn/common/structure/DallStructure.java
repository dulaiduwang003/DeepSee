package com.cn.common.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class DallStructure implements Serializable {

    private String requestUrl;

    private List<String> keyList;

}
