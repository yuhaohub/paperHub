package com.yuhao.yupicturebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.yupicturebackend.model.entity.Picture;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author hyh
* @description 针对表【picture(图片)】的数据库操作Mapper
* @createDate 2025-02-17 11:09:30
* @Entity com.yuhao.yupicturebackend.model.entity.Picture
*/
public interface PictureMapper extends BaseMapper<Picture> {
            void batchUpdateLikeCount(@Param("countMap") Map<Long,Long> countMap);
}




