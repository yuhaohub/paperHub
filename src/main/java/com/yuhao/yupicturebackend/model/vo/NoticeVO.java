package com.yuhao.yupicturebackend.model.vo;


import lombok.Data;

import java.util.Date;

@Data
public class NoticeVO {
    /**
     * 消息ID
     */
    Long id;
    /**
     * 消息类型
     * ANNOUNCEMENT("公告",0),
     * REVIEW("审核通知",1);
     */
    int type ;
    /**
     * 消息标题
     */
    String title;
    /**
     * 消息内容
     */

    String content;

    /**
     * 消息时间
     */
    Date noticeTime;
    /**
     * 是否已读
     */
    boolean read = false;
}
