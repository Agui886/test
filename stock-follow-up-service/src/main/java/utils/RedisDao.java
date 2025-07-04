package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedisTemplateLockExecutor;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisDao {

	@Resource
	private RedisTemplate<String, Object> redisTemplate;
	
    @Value("${lock4j.expire}")
    private long expire;

    @Value("${lock4j.acquire-timeout}")
    private long timeout;

    @Value("${lock4j.lock-key-prefix}")
    private String keyPrefix;

    public boolean set(String key, String string) {
        try {
            redisTemplate.opsForValue().set(key, string);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setString(String key, String string) {
        try {
            redisTemplate.opsForValue().set(key, string);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象,并设置生命周期
    public boolean setString(String key, String string, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, string, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setBean(String key, Object value) {
        try {
        	redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setBean(String key, Object value, boolean flage) {
        try {
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 存一个对象
    public boolean setBean(String key, Object value, long time, TimeUnit timeUnit) {
        try {
        	redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    // 返回指定对象
    public String getStr(String key) {
        // 判断key是不是空
        if (StringUtils.isNotBlank(key)) {
            // 判断是否能从redis取出值
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null) {
                return (String) obj;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // 返回指定对象
    public <T> T getBean(String key, Class<T> beanClass) {
        // 判断key是不是空
        if (StringUtils.isNotBlank(key)) {
            // 判断是否能从redis取出值
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj != null && obj != "") {
                if (beanClass.isInstance(String.class)) {
                    return (T) obj;
                } else {
                    return JSONUtil.toBean(JSONUtil.toJsonStr(obj), beanClass);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // 返回集合对象
    public <T> List<T> getBeanList(String key, Class<T> beanClass) {
        String s = getStr(key);
        List<T> ts = JSONUtil.toList(s, beanClass);
        return ts;
//        if (ts.size() == 0) {
//            return null;
//        } else {
//            return ts;
//        }
    }

    // 删除缓存
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    // 模糊查询key
    public Set<String> keys(String s) {
        Set<String> keys = redisTemplate.keys(s);
        return keys;
    }

    // 设置生命周期
    public void expire(String key, long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    @Resource
    private LockTemplate lockTemplate;

    /**
     * 加锁
     *
     * @param key         redis的key
     * @param expireTime  key的有效时间
     * @param waitTimeout 等待获取锁超时时间
     * @return 有值, 加锁成功, 没有值加锁失败
     */
    public LockInfo lock(String key, long expireTime, long waitTimeout) {
        key = keyPrefix + "_" + key;
        return lockTemplate.lock(key, expireTime, waitTimeout, RedisTemplateLockExecutor.class);
    }

    /**
     * 加锁,使用环境变量配置的默认时间
     *
     * @param key redis的key
     * @return 有值, 加锁成功, 没有值加锁失败
     */
    public LockInfo lockDefaultTime(String key) {
        key = keyPrefix + "_" + key;
        return lockTemplate.lock(key, expire, timeout, RedisTemplateLockExecutor.class);
    }

    // 释放锁

    /**
     * 释放锁
     *
     * @param lockInfo 调用lock方法 返回获取到的对象
     * @return
     */
    public boolean releaseLock(LockInfo lockInfo) {
        return lockTemplate.releaseLock(lockInfo);
    }
    
    
    
    /**
     * 获取缓存过期时间
     * @param type
     * @param key
     * @param timeUnit
     * @return
     */
    public Long getExpire(String key,TimeUnit timeUnit) {
        // 判断key是不是空
        if (StringUtils.isNotBlank(key)) {
            // 判断是否能从redis取出值
            Long expire = redisTemplate.getExpire(key,timeUnit);
            if (expire != null) {
                return expire;
            } else {
                return null;
            }
        } else {
            return null;
        }
	}

}
