package com.cn.common.structure;

import com.cn.configuration.ChatGptConfiguration;
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

    private ChatGptConfiguration.ImageRecognitionConfig imageRecognitionConfig;

    private List<ChatGptConfiguration.Model> modelList;


}
