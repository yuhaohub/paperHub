package com.yuhao.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

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
    private Long pictureId;

    /**
     * 点赞状态 0取消点赞 1点赞
     */
    private Integer likeStatus;
}
