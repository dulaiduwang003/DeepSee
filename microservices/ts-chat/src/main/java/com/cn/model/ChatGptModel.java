package com.cn.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * The type Chat gpt model.
 */
@Data
@Accessors(chain = true)
public class ChatGptModel implements Serializable {

    private String model;

    private int top_p = 1;

    private List<Messages> messages;

    private boolean stream = true;

    private int max_tokens = 2048;

    private int temperature = 1;


    @Data
    public static class Messages {

        private String role;

        private String content;
    }



}
