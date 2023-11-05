package com.cn.common.structure;

import com.cn.configuration.ChatGptDefaultConfiguration;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class ChatGptStructure implements Serializable {

    private String requestUrl;

    private List<String> lowLevelKeyList;

    private List<String> seniorKeyList;

    private List<ChatGptDefaultConfiguration.Model> modelList;

}
