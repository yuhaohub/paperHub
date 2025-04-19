package com.yuhao.yupicturebackend.job;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yuhao.yupicturebackend.mapper.LikeRecordMapper;
import com.yuhao.yupicturebackend.mapper.PictureMapper;
import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import com.yuhao.yupicturebackend.model.enums.LikeStatusEnum;
import com.yuhao.yupicturebackend.service.LikeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 定时将redis中的临时数据同步到数据库
 */


@Component
@Slf4j
public class SyncLikeToDBJob {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    PictureMapper pictureMapper;
    @Resource
    LikeRecordMapper likeRecordMapper;
    @Resource
    @Qualifier("likeService")
    private LikeRecordService likeService;
    public void syncLikeToDBByDate(String date){
        //获取临时点赞的数据
        String tempLikeKey = date;
        Map<Object, Object> allTempLikeMap = redisTemplate.opsForHash().entries(tempLikeKey);
        boolean likeMapEmpty = CollUtil.isEmpty(allTempLikeMap);
        if(likeMapEmpty){
            return;
        }
        //收集数据
        Map<Long,Long> picLikeCountMap = new HashMap<>();
        ArrayList<LikeRecord> likeList = new ArrayList<>();
        UpdateWrapper<LikeRecord> updateWrapper = new UpdateWrapper<>();
        //逻辑删除
        boolean needRemove = false;
        for (Object userIdPicIdObj : allTempLikeMap.keySet()){
            //分割
            String userIdPicId = (String) userIdPicIdObj;
            String[] userIdAndPicId = userIdPicId.split(StrPool.COLON);
            Long userId = Long.valueOf(userIdAndPicId[0]);
            Long picId = Long.valueOf(userIdAndPicId[1]);
            // -1 取消点赞，1 点赞
            Integer likeType = Integer.valueOf(allTempLikeMap.get(userIdPicId).toString());
            if(likeType == LikeStatusEnum.LIKE.getValue()){
                LikeRecord likeRecord = new LikeRecord();
                likeRecord.setUserId(userId);
                likeRecord.setPicId(picId);
                likeRecord.setStatus(1);
                likeList.add(likeRecord);
            }else if(likeType == -1){
                //拼接查询条件
                needRemove = true;
                updateWrapper.eq("userId", userId)
                        .eq("picId", picId)
                        .set("status", 0);
            }else {
                if (likeType != 0) {
                    log.warn("数据异常：{}", userId + "," + picId + "," + likeType);
                }
                continue;
            }
            //计算点赞增量
            picLikeCountMap.put(picId, picLikeCountMap.getOrDefault(picId, 0L) + likeType);
            // 批量插入,当存在历史记录修改status
            if(!likeList.isEmpty()){
                likeRecordMapper.batchInsertOrUpdate(likeList);
            }

            // 批量删除
            if (needRemove) {
                likeService.update(updateWrapper);
            }
            // 批量更新壁纸点赞量
            if (!picLikeCountMap.isEmpty()) {
                pictureMapper.batchUpdateLikeCount(picLikeCountMap);
            }
            // todo ,异步删除,改用消息队列
            new Thread(() -> {
                redisTemplate.delete(tempLikeKey);
            }).start();
        }
    }
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("开始执行");
        DateTime nowDate = DateUtil.date();
        int second = DateUtil.second(nowDate);
        int tenSecondBucket = second / 10;

        if (tenSecondBucket == 0) {
            // 回退到上一分钟的 50-59 秒段
            nowDate = DateUtil.offsetMinute(nowDate, -1);
            tenSecondBucket = 5;
        } else {
            tenSecondBucket -= 1;
        }

        String date = DateUtil.format(nowDate, "HH:mm:") + (tenSecondBucket * 10);

        syncLikeToDBByDate(date);
        log.info("临时数据同步完成");
    }
}
