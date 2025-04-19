package com.yuhao.yupicturebackend.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.yupicturebackend.constant.RedisLuaScriptConstant;
import com.yuhao.yupicturebackend.exception.ErrorCode;
import com.yuhao.yupicturebackend.exception.ThrowUtils;
import com.yuhao.yupicturebackend.mapper.LikeRecordMapper;
import com.yuhao.yupicturebackend.model.dto.picture.PictureLikeRequest;
import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import com.yuhao.yupicturebackend.model.entity.Picture;
import com.yuhao.yupicturebackend.model.entity.User;
import com.yuhao.yupicturebackend.model.enums.LikeStatusEnum;
import com.yuhao.yupicturebackend.model.enums.LuaStatusEnum;
import com.yuhao.yupicturebackend.model.vo.PictureVO;
import com.yuhao.yupicturebackend.service.LikeRecordService;
import com.yuhao.yupicturebackend.service.PictureService;
import com.yuhao.yupicturebackend.service.UserService;
import com.yuhao.yupicturebackend.untils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("likeService")
@Slf4j
@RequiredArgsConstructor
public class LikeServiceRedisImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {
    private final UserService userService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final PictureService pictureService;
    @Override
    public boolean like(LikeRecord likeRecord, HttpServletRequest request) {

        ThrowUtils.throwIf(likeRecord == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long picId = likeRecord.getPicId();
        //时间片
        String timeSlice = getTimeSlice();
        //key
        String tempLikeKey = RedisKeyUtil.getTempLikeKey(timeSlice);
        String userLikeKey = RedisKeyUtil.getUserLikeKey(loginUser.getId());
        //执行lua脚本
        long result =-1;
         result = redisTemplate.execute(RedisLuaScriptConstant.LIKE_SCRIPT, Arrays.asList(tempLikeKey,userLikeKey),loginUser.getId(),picId);
        if(LuaStatusEnum.FAIL.getValue() == result){
            //执行取消点赞lua脚本
            result = redisTemplate.execute(RedisLuaScriptConstant.UNLIKE_SCRIPT,Arrays.asList(tempLikeKey,userLikeKey),loginUser.getId(),picId);

        }
        return LuaStatusEnum.SUCCESS.getValue() == result;
    }

    private String getTimeSlice() {
        DateTime nowDate = DateUtil.date();
        return DateUtil.format(nowDate,"HH:mm:")+(DateUtil.second(nowDate)/10)*10;
    }

    @Override
    public boolean likeUsingRedis(LikeRecord likeRecord, HttpServletRequest request) {
        return false;
    }

    @Override
    public List<PictureVO> getUserLikeRecord(Long userId) {
        List<Long>  picIdList = this.lambdaQuery().eq(LikeRecord::getUserId, userId).eq(LikeRecord::getStatus, LikeStatusEnum.LIKE.getValue()).list().stream().map(LikeRecord::getPicId).collect(Collectors.toList());
        if(picIdList.isEmpty()){
            return null;
        }
        //根据图片id查询图片信息并返回
        List<Picture> pictureList = pictureService.listByIds(picIdList).stream().collect(Collectors.toList());
        //转成VO对象返回
        return pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
    }

    @Override
    public Boolean hasLike(Long picId, Long userId) {
        return redisTemplate.opsForHash().hasKey(RedisKeyUtil.getUserLikeKey(userId),picId.toString());
    }

    @Override
    public QueryWrapper<LikeRecord> getQueryWrapper(PictureLikeRequest pictureLikeQueryRequest) {
        return null;
    }
}
