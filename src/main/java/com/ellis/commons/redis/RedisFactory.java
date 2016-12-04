package com.ellis.commons.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author ellis.luo
 * @date 16/11/8 下午3:35
 * @description
 */
public class RedisFactory
{
    private final Logger logger = LoggerFactory.getLogger(RedisFactory.class);

    // ----------------------------------【conn】--------------------------------------
    private String CONN_IP;
    private int CONN_PORT;
    private String CONN_PWD;
    private int CONN_TIMEOUT;
    // ----------------------------------【config】--------------------------------------
    // 最大活跃数：能同时建立的最大链接个数
    private int MAX_ACTIVE;
    // 最大空闲数：大于这个值，则进行回收
    private int MAX_IDLE;
    // 最小空闲数：低于这个值，则创建新的链接
    private int MIN_IDLE;
    // 最大等待时间 ms
    private int MAX_WAIT;

    private JedisPool pool;

    private RedisFactory()
    {
    }

    private void initJedis()
    {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMinIdle(MIN_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);

        if (!CONN_PWD.equals(""))
        {
            pool = new JedisPool(config, CONN_IP, CONN_PORT, CONN_TIMEOUT, CONN_PWD);
        }
        else
        {
            pool = new JedisPool(config, CONN_IP, CONN_PORT, CONN_TIMEOUT);
        }
    }

    public Jedis getResource()
    {

        if (null == pool)
        {
            synchronized (this)
            {
                if (null == pool)
                {
                    initJedis();
                }
            }
        }

        if (null == pool)
        {
            logger.error("Jedis not initialized.");
            throw new RuntimeException("Jedis not initialized.");
        }

        return pool.getResource();
    }

    public void returnResource(Jedis jedis)
    {
        if (pool != null)
        {
            pool.returnResource(jedis);
        }
    }

    public void destroy()
    {
        if (pool != null)
        {
            pool.destroy();
        }
    }

    public void setCONN_IP(String CONN_IP)
    {
        this.CONN_IP = CONN_IP;
    }

    public void setCONN_PORT(int CONN_PORT)
    {
        this.CONN_PORT = CONN_PORT;
    }

    public void setCONN_PWD(String CONN_PWD)
    {
        this.CONN_PWD = CONN_PWD;
    }

    public void setCONN_TIMEOUT(int CONN_TIMEOUT)
    {
        this.CONN_TIMEOUT = CONN_TIMEOUT;
    }

    public void setMAX_ACTIVE(int MAX_ACTIVE)
    {
        this.MAX_ACTIVE = MAX_ACTIVE;
    }

    public void setMAX_IDLE(int MAX_IDLE)
    {
        this.MAX_IDLE = MAX_IDLE;
    }

    public void setMIN_IDLE(int MIN_IDLE)
    {
        this.MIN_IDLE = MIN_IDLE;
    }

    public void setMAX_WAIT(int MAX_WAIT)
    {
        this.MAX_WAIT = MAX_WAIT;
    }
}
