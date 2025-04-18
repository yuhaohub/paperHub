package com.yuhao.yupicturebackend.untils;


import com.yuhao.yupicturebackend.constant.LikeConstant;

/**
 * redis key的工具类
 */
public class RedisKeyUtil {


    public String getUserLikeKey(Long userId){
        return LikeConstant.USER_Like_KEY_PREFIX+userId;
    }


}
