package com.cn.common.structure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class BehaviorStructure implements Serializable {
    private String author;
    private String botName;
    private String language;
    private List<String> sensitiveWords;
    private String sensitiveSymbol;
}
