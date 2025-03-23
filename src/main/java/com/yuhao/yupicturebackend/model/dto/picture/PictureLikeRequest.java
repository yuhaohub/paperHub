package com.yuhao.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PictureLikeRequest implements Serializable {
  
    /**  
     * id  
     */  
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 图片id
     */
    private Long picId;

    /**
     * 点赞状态 0取消点赞 1点赞
     */
    private Integer likeStatus;
    /**
     * 更新时间
     */
    private Date updateTime;
    //支持查询一段时间的点赞记录
    /**
     * 开始编辑时间
     */
    private Date startEditTime;

    /**
     * 结束编辑时间
     */
    private Date endEditTime;
}
