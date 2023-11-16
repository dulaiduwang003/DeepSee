package com.cn.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.entity.TsDrawing;
import com.cn.mapper.TsDrawingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ScheduledTask {

    private final TsDrawingMapper tsDrawingMapper;

    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void yourTask() {
        final List<Long> list = tsDrawingMapper.selectList(
                new QueryWrapper<TsDrawing>()
                        .lambda()
                        .lt(TsDrawing::getCreatedTime, LocalDateTime.now().minusHours(1))
                        .select(TsDrawing::getDrawingId)
        ).stream().map(TsDrawing::getDrawingId).toList();
        if (!list.isEmpty()) {
            //执行批量删除
            tsDrawingMapper.delete(new QueryWrapper<TsDrawing>().in("drawing_id", list));
        }
    }
}
