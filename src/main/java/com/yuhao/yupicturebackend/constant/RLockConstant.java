package com.yuhao.yupicturebackend.constant;

/**
 * redis锁
 */
public interface RLockConstant {
     //锁前缀
     String LIKE_LOCK_PREFIX = "like:lock:user:";


     //waitTimeout 尝试获取锁的最大等待时间，超过这个值，则认为获取锁失败
     int WAIT_TIMEOUT = 5;
     //leaseTime   锁的持有时间,超过这个时间锁会自动失效（值应设置为大于业务处理的时间，确保在锁有效期内业务能处理完）
     int LEASE_TIME = 10;

}
