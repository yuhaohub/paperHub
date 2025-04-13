package com.yuhao.yupicturebackend.controller;


import com.yuhao.yupicturebackend.common.BaseResponse;
import com.yuhao.yupicturebackend.common.ResultUtils;
import com.yuhao.yupicturebackend.exception.ErrorCode;
import com.yuhao.yupicturebackend.exception.ThrowUtils;
import com.yuhao.yupicturebackend.model.entity.UserSystemNotice;
import com.yuhao.yupicturebackend.model.vo.NoticeVO;
import com.yuhao.yupicturebackend.service.UserSystemNoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private UserSystemNoticeService userSystemNoticeService;




    /**
     * 前段轮询接口
     */

    @GetMapping("//pull")
    public BaseResponse<List<NoticeVO>> noticePull(Long userId ){
        List<NoticeVO> notices = userSystemNoticeService.findNoticeByUserId(userId);
        return ResultUtils.success(notices);
    }

    /**
     * 修改信息状态
     */
    @PostMapping
    public BaseResponse<Boolean> updateNoticeState(Long noticeId,Long userId){
        UserSystemNotice oldResult = userSystemNoticeService.lambdaQuery().eq(UserSystemNotice::getSystemNoticeId, noticeId).eq(UserSystemNotice::getRecipientId, userId).one();
        ThrowUtils.throwIf(oldResult == null, ErrorCode.SYSTEM_ERROR,"信息不存在");
        if(oldResult.getState() == 1){
            return ResultUtils.success(true);
        }
        UserSystemNotice notice = new UserSystemNotice();
        BeanUtils.copyProperties(oldResult, notice);
        notice.setState(1);
        boolean result = userSystemNoticeService.updateById(notice);
        return ResultUtils.success(result);
    }
}
