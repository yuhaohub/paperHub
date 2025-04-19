package com.yuhao.yupicturebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author hyh
* @description 针对表【like_record(点赞记录表)】的数据库操作Mapper
* @createDate 2025-03-03 14:42:46
* @Entity com.yuhao.yupicturebackend.model.entity.LikeRecord
*/
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
    /**
     * 批量插入，冲突时更新status（MySQL专用）
     */

    int batchInsertOrUpdate(@Param("list") List<LikeRecord> recordList);
}




