package com.cn.task;

import com.cn.mapper.TsDialogueDrawingMapper;
import com.cn.utils.UploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ScheduledTask {

    private final UploadUtil uploadUtil;

    private final TsDialogueDrawingMapper tsDialogueDrawingMapper;

    @Scheduled(cron = "0 0 * * * ?")
    public void dialogueImageGeneration() {
//        final List<TsDialogueDrawing> list = tsDialogueDrawingMapper.selectList(
//                new QueryWrapper<TsDialogueDrawing>()
//                        .lambda()
//                        .lt(TsDialogueDrawing::getCreatedTime, LocalDateTime.now().minusHours(1))
//                        .select(TsDialogueDrawing::getDialogueDrawingId, TsDialogueDrawing::getUrl)
//        ).stream().toList();
//        final List<Long> ids = list.stream().map(TsDialogueDrawing::getDialogueDrawingId).toList();
//        if (!ids.isEmpty()) {
//            //执行批量删除
//            tsDialogueDrawingMapper.delete(new QueryWrapper<TsDialogueDrawing>().in("drawing_id", ids));
//            list.forEach(l -> uploadUtil.deletedFile(l.getUrl()));
//        }


    }
}
