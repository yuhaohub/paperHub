package com.yuhao.yupicturebackend.model.enums;


import lombok.Getter;

@Getter
public enum LuaStatusEnum {
    SUCCESS(1L),
    FAIL(-1L),
    ;
    private final Long value;
    LuaStatusEnum(long value){
        this.value = value;
    }
}
