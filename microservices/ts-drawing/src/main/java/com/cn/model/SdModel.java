package com.cn.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@SuppressWarnings("all")
public class SdModel implements Serializable {

    private List<String> init_images;

    private String mask;
//
//    private Double denoising_strength;

    private String prompt;

    private Long width;

    private Long height;

    private String sampler_index;

    private Integer steps;

    private String negative_prompt;

    private Override override_settings;

    @Data
    @Accessors(chain = true)
    public static class Override implements Serializable {

        private String sd_model_checkpoint;

        private String sd_vae;
    }

}
