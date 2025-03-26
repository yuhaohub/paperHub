package com.yuhao.yupicturebackend.model.enums;


import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 接受通知的用户 id;0代表全体用户,1代表vip用户,其他则为具体用户id
 */
@Getter
public enum NoticeUserEnum {

    ALL(0, "all"),
    VIP(1, "vip"),
    SINGLE(2, "single");
    ;
    private final String text;
    private final Integer value;


    NoticeUserEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
    /**
     * 根据 value 获取枚举类型
     */
    public static NoticeUserEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (NoticeUserEnum noticeUserEnum : NoticeUserEnum.values()) {
            if (noticeUserEnum.value == value) {
                return noticeUserEnum;
            }
        }
        return null;
    }
}
