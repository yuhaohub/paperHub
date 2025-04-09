package com.yuhao.yupicturebackend.model.enums;


import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

//通知类型枚举
@Getter
public enum NoticeTypeEnum {
    ANNOUNCEMENT("公告",0),
    REVIEW("审核通知",1),
    PRIVATE("私信",2);

    private final String text;
    private final int value;

    NoticeTypeEnum(String text, int value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举类型
     */
    public static NoticeTypeEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (NoticeTypeEnum noticeTypeEnum : NoticeTypeEnum.values()) {
            if (noticeTypeEnum.value == value) {
                return noticeTypeEnum;
            }
        }
        return null;
    }


}
