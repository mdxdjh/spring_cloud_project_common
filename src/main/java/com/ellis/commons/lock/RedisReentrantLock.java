package com.ellis.commons.lock;

import com.ellis.commons.redis.RedisClient;
import org.apache.commons.lang.StringUtils;

/**
 * @author smartlv
 */
public class RedisReentrantLock
{
    private RedisClient redisClient;
    // 单位毫秒,锁超时
    private long lockTimeOut;
    // 获取不到锁，等待多久
    private long perSleep;

    /**
     * 得不到锁立即返回，得到锁返回设置的超时时间
     * 
     * @param key
     * @return
     */
    public long tryLock(String key)
    {
        // 得到锁后设置的过期时间，未得到锁返回0
        long expireTime = 0;

        expireTime = System.currentTimeMillis() + lockTimeOut + 1;
        if (redisClient.setnx(key, String.valueOf(expireTime)) == 1)
        {
            // 得到了锁返回
            return expireTime;
        }
        else
        {
            String curLockTimeStr = redisClient.get(key);
            // 判断是否过期
            if (StringUtils.isBlank(curLockTimeStr) || System.currentTimeMillis() > Long.valueOf(curLockTimeStr))
            {
                expireTime = System.currentTimeMillis() + lockTimeOut + 1;

                curLockTimeStr = redisClient.getSet(key, String.valueOf(expireTime));
                // 仍然过期,则得到锁
                if (StringUtils.isBlank(curLockTimeStr) || System.currentTimeMillis() > Long.valueOf(curLockTimeStr))
                {
                    return expireTime;
                }
                else
                {
                    return 0;
                }
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * 得到锁返回设置的超时时间，得不到锁等待
     * 
     * @param key
     * @return
     * @throws InterruptedException
     */
    public long lock(String key) throws InterruptedException
    {
        long starttime = System.currentTimeMillis();
        long sleep = (perSleep == 0 ? lockTimeOut / 10 : perSleep);
        // 得到锁后设置的过期时间，未得到锁返回0
        long expireTime = 0;
        for (;;)
        {
            expireTime = System.currentTimeMillis() + lockTimeOut + 1;
            if (redisClient.setnx(key, String.valueOf(expireTime)) == 1)
            {
                // 得到了锁返回
                return expireTime;
            }
            else
            {
                String curLockTimeStr = redisClient.get(key);
                // 判断是否过期
                if (StringUtils.isBlank(curLockTimeStr) || System.currentTimeMillis() > Long.valueOf(curLockTimeStr))
                {
                    expireTime = System.currentTimeMillis() + lockTimeOut + 1;

                    curLockTimeStr = redisClient.getSet(key, String.valueOf(expireTime));
                    // 仍然过期,则得到锁persistCache
                    if (StringUtils.isBlank(curLockTimeStr)
                            || System.currentTimeMillis() > Long.valueOf(curLockTimeStr))
                    {
                        return expireTime;
                    }
                    else
                    {
                        Thread.sleep(sleep);
                    }
                }
                else
                {
                    Thread.sleep(Long.valueOf(curLockTimeStr) - System.currentTimeMillis());
                }
            }
            if (lockTimeOut > 0 && ((System.currentTimeMillis() - starttime) >= lockTimeOut))
            {
                expireTime = 0;
                return expireTime;
            }
        }

    }

    /**
     * 先判断自己运行时间是否超过了锁设置时间，是则不用解锁
     * 
     * @param key
     * @param expireTime
     */
    public void unlock(String key, long expireTime)
    {
        if (System.currentTimeMillis() - expireTime > 0)
        {
            return;
        }
        String curLockTimeStr = redisClient.get(key);
        if (StringUtils.isNotBlank(curLockTimeStr) && Long.valueOf(curLockTimeStr) > System.currentTimeMillis())
        {
            redisClient.del(key);
        }
    }

    /**
     * @param lockTimeOut
     *        the lockTimeOut to set
     */
    public void setLockTimeOut(long lockTimeOut)
    {
        this.lockTimeOut = lockTimeOut;
    }

    /**
     * @return the lockTimeOut
     */
    public long getLockTimeOut()
    {
        return lockTimeOut;
    }

    /**
     * @param perSleep
     *        the perSleep to set
     */
    public void setPerSleep(long perSleep)
    {
        this.perSleep = perSleep;
    }

    public RedisClient getRedisClient()
    {
        return redisClient;
    }

    public void setRedisClient(RedisClient redisClient)
    {
        this.redisClient = redisClient;
    }

}
