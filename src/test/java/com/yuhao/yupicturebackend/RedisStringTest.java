package com.yuhao.yupicturebackend;

import com.yuhao.yupicturebackend.constant.LikeConstant;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = YuPictureBackendApplication.class)

public class RedisStringTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Test
    public void testRedisStringOperations() {
        // 获取操作对象
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // Key 和 Value
        String key = "testKey";
        String value = "testValue";

        // 1. 测试新增或更新操作
        valueOps.set(key, value);
        String storedValue = valueOps.get(key);
        assertEquals(value, storedValue, "存储的值与预期不一致");

        // 2. 测试修改操作
        String updatedValue = "updatedValue";
        valueOps.set(key, updatedValue);
        storedValue = valueOps.get(key);
        assertEquals(updatedValue, storedValue, "更新后的值与预期不一致");

        // 3. 测试查询操作
        storedValue = valueOps.get(key);
        assertNotNull(storedValue, "查询的值为空");
        assertEquals(updatedValue, storedValue, "查询的值与预期不一致");

        // 4. 测试删除操作
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull(storedValue, "删除后的值不为空");
    }
    @Test
    public void testRedisHashOperations(){
        //assertNotNull(redisTemplate, "RedisTemplate 未正确注入");
        //redisTemplate.opsForHash().put(LikeConstant.USER_THUMB_KEY_PREFIX+"1890648886621085698","1892092981809893378","1903631261953019906");
        RMap<Object, Object> map = redissonClient.getMap(LikeConstant.USER_Like_KEY_PREFIX + "1890648886621085698");
        map.put("1892092981809893378","1903631261953019906");
        map.get(LikeConstant.USER_Like_KEY_PREFIX + "1890648886621085698");
    }
}
