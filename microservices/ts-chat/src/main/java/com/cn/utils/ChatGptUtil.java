package com.cn.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * The type Forbidden words util.
 */
@Component
@SuppressWarnings("all")
public class ChatGptUtil {

    /**
     * 解析CHATGPT数据体 flux
     *
     * @param data the data
     * @return the boolean
     */
    public String dataParsing(final String data) {
        if (JSON.isValidObject(data)) {
            JSONObject jsonObject = JSONObject.parseObject(data);
            JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject delta = choices.getJSONObject(0).getJSONObject("delta");
                if (delta.containsKey("content")) {
                    return delta.getString("content");
                }

            }
        }
        return "";
    }

    /**
     * 文本敏感词检查
     *
     * @param data
     * @return
     */
    public boolean isSusceptible(final String data) {
        String processedInput = data.toUpperCase().replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile("chat");
        return pattern.matcher(processedInput).find();
    }

}
