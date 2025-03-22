package com.yuhao.yupicturebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.yupicturebackend.exception.ErrorCode;
import com.yuhao.yupicturebackend.exception.ThrowUtils;
import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import com.yuhao.yupicturebackend.model.enums.LikeStatusEnum;
import com.yuhao.yupicturebackend.service.LikeRecordService;
import com.yuhao.yupicturebackend.mapper.LikeRecordMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author hyh
* @description 针对表【like_record(点赞记录表)】的数据库操作Service实现
* @createDate 2025-03-03 14:42:46
*/
@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord>
    implements LikeRecordService{

    @Override
    public boolean like(Long userId, Long pictureId) {
        //校验参数
        ThrowUtils.throwIf(userId == null || pictureId == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        //查询点赞记录是否存在

        //保存到数据库
        LikeRecord likeRecord = new LikeRecord();
        likeRecord.setUserId(userId);
        likeRecord.setPicId(pictureId);
        likeRecord.setStatus(LikeStatusEnum.LIKE.getValue());
        likeRecord.setCreateTime(new Date());
        boolean result =this.save(likeRecord);
        return result;
    }

    @Override
    public void unlike(Long userId, Long pictureId) {

    }

    @Override
    public LikeRecord getUserLikeRecord(Long userId, Long pictureId) {
        return null;
    }

    @Override
    public Integer getPictureLikeCount(Long pictureId) {
        return null;
    }
}




