package com.cn.service;

import com.cn.dto.AddModelDto;
import com.cn.vo.ModelVo;

import java.util.List;

/**
 * The interface Model service.
 */
public interface ModelService {


    /**
     * Gets list of models.
     *
     * @return the list of models
     */
    List<ModelVo> getListOfModels();


    /**
     * Add a model.
     *
     * @param dto the dto
     */
    void addAModel(final AddModelDto dto);


    /**
     * removes the model based on coordinates
     *
     * @param index the index
     */
    void delAModel(final Integer index);

}
