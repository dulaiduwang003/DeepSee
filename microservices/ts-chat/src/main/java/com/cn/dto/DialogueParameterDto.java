package com.cn.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DialogueParameterDto {
    //数据源
    private String messages;

    //对话类型
    private String type;

    //额外参数
    private String extra;


    @Data
    @Accessors(chain = true)
    public static class Extra {

        private Integer modelIndex;

        private Boolean isFiltration;

    }
}
