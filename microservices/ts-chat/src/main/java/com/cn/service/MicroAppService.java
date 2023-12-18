package com.cn.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cn.dto.UseMicroTemplateDto;
import com.cn.vo.MicroAppVo;

public interface MicroAppService {


    /**
     * Gets micro application list.
     *
     * @return the micro application list
     */
    IPage<MicroAppVo> getMicroAppPage(final int pageNum);


    /**
     * Search micro application list.
     *
     * @param prompt the prompt
     * @return the list
     */
    IPage<MicroAppVo.Vo> searchMicroApp(final String prompt, final int pageNum);


    /**
     * Search micro application list.
     */
    void useMicroApp(UseMicroTemplateDto dto);


}
