package com.cn.service.impl;

import com.cn.common.ChatGptCommon;
import com.cn.configuration.ChatGptDefaultConfiguration;
import com.cn.service.ModelService;
import com.cn.vo.ModelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    @Override
    public List<ModelVo> getModelList() {

        return IntStream.range(0, ChatGptCommon.STRUCTURE.getModelList().size())
                .mapToObj(index -> {
                    final ChatGptDefaultConfiguration.Model model = ChatGptCommon.STRUCTURE.getModelList().get(index);
                    return new ModelVo().setModelIndex(index).setModelName(model.getModelName());
                }).toList();
    }

}
