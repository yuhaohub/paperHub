package com.yuhao.yupicturebackend.model.enums;


import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

//点赞状态枚举类
@Getter
public enum LikeStatusEnum {
    LIKE("点赞",1),
    UNLIKE("取消点赞",0);

    private final String text;
    private final int value;

    LikeStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static LikeStatusEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (LikeStatusEnum likeStatusEnum : LikeStatusEnum.values()) {
            if (likeStatusEnum.value == value) {
                return likeStatusEnum;
            }
        }
        return null;
    }
}
