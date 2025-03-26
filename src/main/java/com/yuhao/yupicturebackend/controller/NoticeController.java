package com.yuhao.yupicturebackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    /**
     * 定时任务，定时从管理员系统通知表中获取通知保存到用户系统通知表中
     */
    //用户主动获取，系统推送
}
