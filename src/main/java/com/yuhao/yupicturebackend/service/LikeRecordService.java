package com.yuhao.yupicturebackend.service;

import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author hyh
* @description 针对表【like_record(点赞记录表)】的数据库操作Service
* @createDate 2025-03-03 14:42:46
*/
public interface LikeRecordService extends IService<LikeRecord> {
    /**
     * 点赞
     */
    boolean like(Long userId, Long pictureId);
    /**
     * 取消点赞
     */
    void unlike(Long userId, Long pictureId);
    /**
     * 查询用户的点赞记录
     */
    LikeRecord getUserLikeRecord(Long userId, Long pictureId);
    /**
     * 查询图片的点赞数
     */
    Integer getPictureLikeCount(Long pictureId);
}
