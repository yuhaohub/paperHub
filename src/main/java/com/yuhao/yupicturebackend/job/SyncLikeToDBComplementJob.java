package com.yuhao.yupicturebackend.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.yuhao.yupicturebackend.constant.LikeConstant;
import com.yuhao.yupicturebackend.untils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 定时将 Redis 中的临时点赞数据同步到数据库的补偿措施
 *
 */
@Component
@Slf4j
public class SyncLikeToDBComplementJob {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SyncLikeToDBJob syncLikeToDBJob;

    @Scheduled(cron = "0 0 2 * * *")
    public void run() {
        log.info("开始补偿数据");
        Set<String> thumbKeys = redisTemplate.keys(RedisKeyUtil.getTempLikeKey("") + "*");
        Set<String> needHandleDataSet = new HashSet<>();
        thumbKeys.stream().filter(ObjUtil::isNotNull).forEach(thumbKey -> needHandleDataSet.add(thumbKey.replace(LikeConstant.TEMP_LIKE_KEY_PREFIX.format(""), "")));

        if (CollUtil.isEmpty(needHandleDataSet)) {
            log.info("没有需要补偿的临时数据");
            return;
        }
        // 补偿数据
        for (String date : needHandleDataSet) {
            syncLikeToDBJob.syncLikeToDBByDate(date);
        }
        log.info("临时数据补偿完成");
    }
}
