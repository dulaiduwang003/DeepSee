package com.cn.common.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class PoolStructure implements Serializable {

    private Integer maximumTask;

    private Integer sdConcurrent;

    private Integer dallConcurrent;

}
