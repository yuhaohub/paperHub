package com.yuhao.yupicturebackend.untils;


import com.yuhao.yupicturebackend.constant.LikeConstant;
import lombok.Getter;

/**
 * redis key的工具类
 */
@Getter
public class RedisKeyUtil {

    /**
     * 获取用户点赞key
     * @param userId
     * @return
     */
    public static String getUserLikeKey(Long userId){
        return LikeConstant.USER_Like_KEY_PREFIX+userId;
    }

    public static String getTempLikeKey(String time){
        return LikeConstant.TEMP_LIKE_KEY_PREFIX.format(time);
    }

}
