package com.cn.service.impl;

import com.cn.common.ChatGptCommon;
import com.cn.common.structure.ChatGptStructure;
import com.cn.configuration.ChatGptDefaultConfiguration;
import com.cn.constants.ChatConstant;
import com.cn.dto.AddModelDto;
import com.cn.service.ModelService;
import com.cn.utils.RedisUtils;
import com.cn.vo.ModelVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final RedisUtils redisUtils;

    @Override
    public List<ModelVo> getListOfModels() {

        return IntStream.range(0, ChatGptCommon.STRUCTURE.getModelList().size())
                .mapToObj(index -> {
                    final ChatGptDefaultConfiguration.Model model = ChatGptCommon.STRUCTURE.getModelList().get(index);
                    return new ModelVo().setModelIndex(index).setModelName(model.getModelName());
                }).toList();
    }

    @Override
    public void addAModel(final AddModelDto dto) {
        // original
        final List<ChatGptDefaultConfiguration.Model> modelList = ChatGptCommon.STRUCTURE.getModelList();

        modelList.add(new ChatGptDefaultConfiguration.Model()
                .setModelName(dto.getModelName())
                .setIsSeniorModel(dto.getIsSeniorModel())
                .setFrequency(dto.getFrequency()));
        //new
        final ChatGptStructure chatGptStructure = ChatGptCommon.STRUCTURE.setModelList(modelList);
        //update the cache data of REDIS
        redisUtils.setValue(ChatConstant.CONFIG, chatGptStructure);
        //update server memory variables
        ChatGptCommon.updateOnlineConfig(chatGptStructure);
    }

    @Override
    public void delAModel(final Integer index) {
        // original
        final List<ChatGptDefaultConfiguration.Model> modelList = ChatGptCommon.STRUCTURE.getModelList();
                modelList.remove(index);
    }
}
