package com.yuhao.yupicturebackend.constant;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisLuaScriptConstant {

    /**
     * 点赞 Lua 脚本
     * KEYS[1]       -- 临时计数键
     * KEYS[2]       -- 用户点赞状态键
     * ARGV[1]       -- 用户 ID
     * ARGV[2]       -- 壁纸 ID
     * 返回:
     * -1: 已点赞
     * 1: 操作成功
     */
    public static final RedisScript<Long> LIKE_SCRIPT = new DefaultRedisScript<>(
            "local tempLikeKey = KEYS[1]\n" +
                    "local userLikeKey = KEYS[2]\n" +
                    "local userId = ARGV[1]\n" +
                    "local picId = ARGV[2]\n" +
                    "\n" +
                    "-- 1. 检查是否已点赞（避免重复操作）\n" +
                    "if redis.call('HEXISTS', userLikeKey, picId) == 1 then\n" +
                    "    return -1\n" +
                    "end\n" +
                    "\n" +
                    "-- 2. 获取旧值（不存在则默认为 0）\n" +
                    "local hashKey = userId .. ':' .. picId\n" +
                    "local oldNumber = tonumber(redis.call('HGET', tempLikeKey, hashKey) or 0)\n" +
                    "\n" +
                    "-- 3. 计算新值\n" +
                    "local newNumber = oldNumber + 1\n" +
                    "\n" +
                    "-- 4. 原子性更新：写入临时计数 + 标记用户已点赞\n" +
                    "redis.call('HSET', tempLikeKey, hashKey, newNumber)\n" +
                    "redis.call('HSET', userLikeKey, picId, 1)\n" +
                    "\n" +
                    "return 1", Long.class
    );

    /**
     * 取消点赞 Lua 脚本
     * 参数同上
     * 返回：
     * -1: 未点赞
     * 1: 操作成功
     */
    public static final RedisScript<Long> UNLIKE_SCRIPT = new DefaultRedisScript<>(
            "local tempLikeKey = KEYS[1]\n" +  // 显式换行
                    "local userLikeKey = KEYS[2]\n" +
                    "local userId = ARGV[1]\n" +
                    "local picId = ARGV[2]\n" +
                    "\n" +  // 空行分隔逻辑块
                    "-- 1. 检查用户是否已点赞（若未点赞，直接返回失败）\n" +
                    "if redis.call('HEXISTS', userLikeKey, picId) ~= 1 then\n" +
                    "    return -1\n" +
                    "end\n" +
                    "\n" +
                    "-- 2. 获取当前临时计数（若不存在则默认为 0）\n" +
                    "local hashKey = userId .. ':' .. picId\n" +
                    "local oldNumber = tonumber(redis.call('HGET', tempLikeKey, hashKey) or 0)\n" +
                    "\n" +
                    "-- 3. 计算新值并更新\n" +
                    "local newNumber = oldNumber - 1\n" +
                    "\n" +
                    "-- 4. 原子性操作：更新临时计数 + 删除用户点赞标记\n" +
                    "redis.call('HSET', tempLikeKey, hashKey, newNumber)\n" +
                    "redis.call('HDEL', userLikeKey, picId)\n" +
                    "\n" +  // 确保return前有换行
                    "return 1",  // 最后一行无需\n（Redis会自动补全）
            Long.class
    );
}