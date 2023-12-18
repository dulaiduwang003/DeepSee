package com.cn.service.impl;

import com.cn.common.ChatGptCommon;
import com.cn.configuration.ChatGptConfiguration;
import com.cn.service.ModelService;
import com.cn.vo.ModelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The type Model service.
 */
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    @Override
    public List<ModelVo> getModelList() {

        return IntStream.range(0, ChatGptCommon.STRUCTURE.getModelList().size())
                .mapToObj(index -> {
                    final ChatGptConfiguration.Model model = ChatGptCommon.STRUCTURE.getModelList().get(index);
                    return new ModelVo().setModelIndex(index).setModelName(model.getModelName());
                }).toList();
    }

}
