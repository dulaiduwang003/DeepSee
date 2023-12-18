package com.cn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.constants.MicroAppConstant;
import com.cn.dto.UseMicroTemplateDto;
import com.cn.entity.TsMicroApp;
import com.cn.entity.TsMicroCategory;
import com.cn.mapper.TsMicroAppMapper;
import com.cn.mapper.TsMicroCategoryMapper;
import com.cn.service.MicroAppService;
import com.cn.utils.RedisUtils;
import com.cn.vo.MicroAppVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * The type Public service.
 */
@Service
@RequiredArgsConstructor
public class MicroAppServiceImpl implements MicroAppService {

    private final TsMicroCategoryMapper tsMicroCategoryMapper;

    private final TsMicroAppMapper tsMicroAppMapper;

    private final RedisUtils redisUtils;
    private final static int ALL_APP_PAGE_SIZE = 2;
    private final static int SEARCH_APP_PAGE_SIZE = 10;

    /**
     * Gets micro application list.
     *
     * @return the micro application list
     */
    @Override
    public IPage<MicroAppVo> getMicroAppPage(final int pageNum) {

        return tsMicroCategoryMapper.selectPage(new Page<>(pageNum, ALL_APP_PAGE_SIZE), new QueryWrapper<TsMicroCategory>()
                .lambda()
                .select(TsMicroCategory::getCategoryName, TsMicroCategory::getMicroCategoryId, TsMicroCategory::getElIcon)).convert(t -> {
            final MicroAppVo vo = new MicroAppVo();
            //填充名字
            vo.setCategoryName(t.getCategoryName());
            //填充ICON
            vo.setElIcon(t.getElIcon());
            final List<TsMicroApp> applications = tsMicroAppMapper.selectList(new QueryWrapper<TsMicroApp>()
                    .lambda()
                    .eq(TsMicroApp::getMicroCategoryId, t.getMicroCategoryId())
                    .select(TsMicroApp::getMicroAppId,
                            TsMicroApp::getTitle,
                            TsMicroApp::getIntroduce,
                            TsMicroApp::getIcon,
                            TsMicroApp::getChineseIssue,
                            TsMicroApp::getEnglishIssue,
                            TsMicroApp::getChineseAnswer,
                            TsMicroApp::getEnglishAnswer
                    )
            );
            List<MicroAppVo.Vo> list;
            //处理子集数据
            if (applications.size() > 5) {
                list = applications.parallelStream().map(this::handleMicroApplicationVo).toList();
                //正常处理
            } else list = applications.stream().map(this::handleMicroApplicationVo).toList();


            return vo.setList(list);
        });


    }

    /**
     * Padding micro application vo micro application vo . vo.
     *
     * @param e the ts micro applications
     * @return the micro application vo . vo
     */
    private MicroAppVo.Vo handleMicroApplicationVo(final TsMicroApp e) {
        final MicroAppVo.Vo s = new MicroAppVo.Vo()
                .setMicroAppId(e.getMicroAppId())
                .setIntroduce(e.getIntroduce())
                .setIcon(e.getIcon())
                .setChineseIssue(e.getChineseIssue())
                .setEnglishIssue(e.getEnglishIssue())
                .setIntroduce(e.getIntroduce())
                .setChineseAnswer(e.getChineseAnswer())
                .setEnglishAnswer(e.getEnglishAnswer())
                .setTitle(e.getTitle());
        //获取阅读量
        final Object value = redisUtils.getValue(MicroAppConstant.MICRO_VISITS + e.getMicroAppId());
        if (value != null) {
            s.setVisits(Long.parseLong(String.valueOf(value)));
        } else s.setVisits(0L);
        return s;
    }


    /**
     * Search micro application list.
     *
     * @param prompt the prompt
     * @return the list
     */
    @Override
    public IPage<MicroAppVo.Vo> searchMicroApp(final String prompt, final int pageNum) {

        return tsMicroAppMapper.selectPage(new Page<>(pageNum, SEARCH_APP_PAGE_SIZE), new QueryWrapper<TsMicroApp>()
                        .lambda()
                        .like(TsMicroApp::getTitle, prompt)
                        .or()
                        .like(TsMicroApp::getIntroduce, prompt)
                        .select(TsMicroApp::getTitle,
                                TsMicroApp::getIcon,
                                TsMicroApp::getMicroAppId,
                                TsMicroApp::getIntroduce,
                                TsMicroApp::getChineseIssue,
                                TsMicroApp::getEnglishIssue,
                                TsMicroApp::getChineseAnswer,
                                TsMicroApp::getEnglishAnswer
                        ))
                .convert(this::handleMicroApplicationVo);


    }

    @Override
    public void useMicroApp(final UseMicroTemplateDto dto) {
        final String key = MicroAppConstant.MICRO_VISITS + dto.getMicroAppId();
//        if (redisUtils.doesItExist(key)) {
//            redisUtils.increment(key, 1);
//        }
        redisUtils.increment(key, 1);
    }
}
