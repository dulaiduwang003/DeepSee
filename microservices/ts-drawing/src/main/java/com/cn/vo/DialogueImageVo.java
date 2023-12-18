package com.cn.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DialogueImageVo {

    private String revisedPrompt;


    private String url;
}
