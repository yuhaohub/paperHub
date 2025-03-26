package com.yuhao.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.yupicturebackend.model.dto.picture.PictureLikeRequest;
import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import com.yuhao.yupicturebackend.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author hyh
* @description 针对表【like_record(点赞记录表)】的数据库操作Service
* @createDate 2025-03-03 14:42:46
*/
public interface LikeRecordService extends IService<LikeRecord> {
    /**
     * 点赞/取消点赞
     */
    boolean like(LikeRecord likeRecord, HttpServletRequest request);

    /**
     * 查询用户的点赞记录
     */
    List<PictureVO> getUserLikeRecord(Long userId);


    QueryWrapper<LikeRecord> getQueryWrapper(PictureLikeRequest pictureLikeQueryRequest);
}
