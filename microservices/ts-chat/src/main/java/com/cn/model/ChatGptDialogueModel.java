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
public class ChatGptDialogueModel implements Serializable {

    private String model;

    private int top_p;

    private List<Messages> messages;

    private boolean stream = true;

    private int max_tokens;

    private int temperature;


    @Data
    public static class Messages {

        private String role;

        private String content;
    }


}
