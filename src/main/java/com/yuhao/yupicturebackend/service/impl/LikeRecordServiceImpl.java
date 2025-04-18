package com.yuhao.yupicturebackend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.yupicturebackend.constant.LikeConstant;
import com.yuhao.yupicturebackend.constant.RLockConstant;
import com.yuhao.yupicturebackend.exception.ErrorCode;
import com.yuhao.yupicturebackend.exception.ThrowUtils;
import com.yuhao.yupicturebackend.mapper.LikeRecordMapper;
import com.yuhao.yupicturebackend.model.dto.picture.PictureLikeRequest;
import com.yuhao.yupicturebackend.model.entity.LikeRecord;
import com.yuhao.yupicturebackend.model.entity.Picture;
import com.yuhao.yupicturebackend.model.entity.User;
import com.yuhao.yupicturebackend.model.enums.LikeStatusEnum;
import com.yuhao.yupicturebackend.model.vo.PictureVO;
import com.yuhao.yupicturebackend.service.LikeRecordService;
import com.yuhao.yupicturebackend.service.PictureService;
import com.yuhao.yupicturebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author hyh
* @description 针对表【like_record(点赞记录表)】的数据库操作Service实现
* @createDate 2025-03-03 14:42:46
*/
@Service
@RequiredArgsConstructor
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord>
    implements LikeRecordService{
    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;
    @Resource
    private TransactionTemplate transactionTemplate;

    private final RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public boolean like(LikeRecord likeRecord ,HttpServletRequest request) {
        //校验参数
        ThrowUtils.throwIf(likeRecord.getPicId() == null, ErrorCode.PARAMS_ERROR, "参数不能为空");

        //查询数据库中图片记录是否存在
        ThrowUtils.throwIf(pictureService.getById(likeRecord.getPicId()) == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        //当前登录用户id
        Long userId = userService.getLoginUser(request).getId();

        /*
        根据用户Id和图片Id，查询点赞记录是否存在
        1)已存在并且状态为点赞状态则取消点赞，状态为取消点赞，则将状态改为点赞
        2)否则保存点赞记录，同时对应图片的点赞计数加一，一个用户只能点赞一次，需要加锁
        */

        // 查询用户点赞记录是否存在

        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("picId", likeRecord.getPicId());
        LikeRecord oldRecord = this.baseMapper.selectOne(queryWrapper);

        //加锁
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            boolean likeResult = transactionTemplate.execute(status->{
                if(oldRecord != null){
                    if(oldRecord.getStatus() == 1){
                        oldRecord.setStatus(0);
                        oldRecord.setUpdateTime(new Date());
                        boolean result = this.updateById(oldRecord);
                        //图片点赞数-1
                        boolean pictureResult = pictureService.lambdaUpdate().eq(Picture::getId, oldRecord.getPicId()).setSql("likeCount = likeCount - 1").update();
                        if(pictureResult) {
                            //取消点赞从redis当中移除点赞记录
                            redisTemplate.opsForHash().delete(LikeConstant.USER_Like_KEY_PREFIX + userId, likeRecord.getPicId().toString());
                        }
                        return result&&pictureResult;
                    }else{
                        oldRecord.setStatus(1);
                        oldRecord.setUpdateTime(new Date());
                        boolean result = this.updateById(oldRecord);
                        //图片点赞数+1
                        boolean pictureResult = pictureService.lambdaUpdate().eq(Picture::getId, oldRecord.getPicId()).setSql("likeCount = likeCount + 1").update();
                        //点赞成功添加到redis当中key为用户id,filed壁纸id，value为点赞记录id
                        if (pictureResult) {
                            redisTemplate.opsForHash().put(LikeConstant.USER_Like_KEY_PREFIX + userId.toString(), likeRecord.getPicId().toString(), oldRecord.getId());
                        }
                        return result&&pictureResult;
                    }
                }
                likeRecord.setUserId(userId);
                likeRecord.setUpdateTime(new Date());
                boolean result =this.save(likeRecord);

                //图片点赞数+1
                boolean pictureResult = pictureService.lambdaUpdate().eq(Picture::getId, likeRecord.getPicId()).setSql("likeCount = likeCount + 1").update();
                boolean success =  result&&pictureResult;
                // 点赞记录存入 Redis
                if (success) {
                    redisTemplate.opsForHash().put(LikeConstant.USER_Like_KEY_PREFIX + userId.toString(), likeRecord.getPicId().toString(), likeRecord.getId());
                }
                return success;
            });
            return likeResult;
        }
    }

    @Override
    public boolean likeUsingRedis(LikeRecord likeRecord, HttpServletRequest request){
        ThrowUtils.throwIf(likeRecord == null,ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        //使用分布式锁
        RLock rlock = redissonClient.getLock(RLockConstant.LIKE_LOCK_PREFIX+loginUser.getId());
        boolean locked = false;
        try {
            //尝试获取锁
            locked = rlock.tryLock(RLockConstant.WAIT_TIMEOUT, RLockConstant.LEASE_TIME, TimeUnit.SECONDS);
            if(!locked){
                log.warn("未能获取分布式锁");
                return false;
            }
            //获取成功
                return   transactionTemplate.execute(status -> {
                    Long picId = likeRecord.getPicId();
                    boolean exists = hasLike(picId,loginUser.getId());
                    if(exists){
                        //已点赞移除redis记录
                        redisTemplate.opsForHash().delete(LikeConstant.USER_Like_KEY_PREFIX+loginUser.getId(),picId.toString());
                        //同步到数据库当中
                        boolean update = this.lambdaUpdate().eq(LikeRecord::getUserId,loginUser.getId()).eq(LikeRecord::getPicId,picId).setSql("status = 0").update();
                        //图片点赞数-1
                        boolean pictureUpdateResult = pictureService.lambdaUpdate().eq(Picture::getId, likeRecord.getPicId()).setSql("likeCount = likeCount - 1").update();
                        return update && pictureUpdateResult;
                    }else{
                        //未命中redis再查询数据库
                        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("userId", loginUser.getId()).eq("picId", likeRecord.getPicId());
                        LikeRecord oldRecord = this.baseMapper.selectOne(queryWrapper);
                        if(oldRecord != null){
                            if(oldRecord.getStatus() == 1){
                                //取消点赞
                                oldRecord.setStatus(0);
                                oldRecord.setUpdateTime(new Date());
                                boolean result = this.updateById(oldRecord);
                                //图片点赞数-1
                                boolean pictureResult = pictureService.lambdaUpdate().eq(Picture::getId, oldRecord.getPicId()).setSql("likeCount = likeCount - 1").update();
                                return result&&pictureResult;
                            }else{
                                oldRecord.setStatus(1);
                                oldRecord.setUpdateTime(new Date());
                                boolean result = this.updateById(oldRecord);
                                //图片点赞数+1
                                boolean pictureResult = pictureService.lambdaUpdate().eq(Picture::getId, oldRecord.getPicId()).setSql("likeCount = likeCount + 1").update();
                                //点赞成功添加到redis当中key为用户id,filed壁纸id，value为点赞记录id
                                if (pictureResult) {
                                    redisTemplate.opsForHash().put(LikeConstant.USER_Like_KEY_PREFIX + loginUser.getId().toString(), likeRecord.getPicId().toString(), oldRecord.getId());
                                }
                                return result&&pictureResult;
                            }
                        }
                        else {
                            // 首次点赞，创建新记录
                            LikeRecord newRecord = new LikeRecord();
                            newRecord.setUserId(loginUser.getId());
                            newRecord.setPicId(picId);
                            newRecord.setStatus(LikeStatusEnum.LIKE.getValue());
                            newRecord.setCreateTime(new Date());
                            newRecord.setUpdateTime(new Date());
                            boolean saveResult = this.save(newRecord);
                            boolean pictureUpdateResult = pictureService.lambdaUpdate()
                                    .eq(Picture::getId, picId)
                                    .setSql("likeCount = likeCount + 1")
                                    .update();
                            if (pictureUpdateResult) {
                                redisTemplate.opsForHash().put(LikeConstant.USER_Like_KEY_PREFIX + loginUser.getId(), picId.toString(), newRecord.getId());
                            }
                            return saveResult && pictureUpdateResult;
                        }
                    }
                });
        } catch (InterruptedException e) {
            throw new RuntimeException("aquire lock fail");
        }
        finally {
            //释放锁
            rlock.unlock();
        }
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
        return redisTemplate.opsForHash().hasKey(LikeConstant.USER_Like_KEY_PREFIX+userId, picId.toString());
    }


    @Override
    public QueryWrapper<LikeRecord> getQueryWrapper(PictureLikeRequest pictureLikeQueryRequest) {
        QueryWrapper<LikeRecord> queryWrapper = new QueryWrapper<>();
        if (pictureLikeQueryRequest == null) {
            return queryWrapper;
        }
        Long id = pictureLikeQueryRequest.getId();
        Long userId = pictureLikeQueryRequest.getUserId();
        Long pictureId = pictureLikeQueryRequest.getPicId();
        Integer likeStatus = pictureLikeQueryRequest.getLikeStatus();
        Date updateTime = pictureLikeQueryRequest.getUpdateTime();
        Date startEditTime = pictureLikeQueryRequest.getStartEditTime();
        Date endEditTime = pictureLikeQueryRequest.getEndEditTime();

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(pictureId), "pictureId", pictureId);
        queryWrapper.eq(ObjUtil.isNotEmpty(likeStatus), "likeStatus", likeStatus);
        //>=editTime
        queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
        //<editTime
        queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);
        //根据更新时间排序
        queryWrapper.orderByAsc("updateTime");
        return queryWrapper;
    }
}




