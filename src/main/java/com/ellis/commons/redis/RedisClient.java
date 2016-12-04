package com.ellis.commons.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import com.ellis.commons.serializer.JsonSerializer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ellis.luo
 * @date 16/11/8 下午3:34
 * @description
 */
public class RedisClient
{
    private RedisFactory factory;
    private final Logger LOG = LoggerFactory.getLogger(RedisClient.class);

    public abstract class Executor<T>
    {

        Jedis jedis;

        Executor()
        {
            jedis = factory.getResource();
        }

        abstract T action();

        public T execute()
        {
            T result = null;
            try
            {
                result = action();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                factory.returnResource(jedis);
            }
            return result;
        }
    }

    public String set(final String key, final String value)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.set(key, value);
            }
        }.execute();
    }

    public String set(final String key, final Object value)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                String str = JsonSerializer.serialize(value);
                return jedis.set(key, str);
            }
        }.execute();
    }

    public String set(final String key, final Object value, final int expire)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                String str = JsonSerializer.serialize(value);
                String result = jedis.set(key, str);
                jedis.expire(key, expire);
                return result;
            }
        }.execute();
    }

    public String set(final String key, final String value, final int expire)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                String result = jedis.set(key, value);
                jedis.expire(key, expire);
                return result;
            }
        }.execute();
    }

    public String get(final String key)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.get(key);
            }
        }.execute();
    }

    public String getSet(final String key, final String value)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.getSet(key, value);
            }
        }.execute();
    }

    public Long setnx(final String key, final String value)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.setnx(key, value);
            }
        }.execute();
    }

    public <T> T get(final String key, final Class<T> clazz)
    {
        return new Executor<T>()
        {
            @Override
            T action()
            {
                String str = jedis.get(key);
                T result = JsonSerializer.deserialize(str, clazz);
                return result;
            }
        }.execute();
    }

    public Long del(final String key)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.del(key);
            }
        }.execute();
    }

    public Boolean exists(final String key)
    {
        return new Executor<Boolean>()
        {
            @Override
            Boolean action()
            {
                return jedis.exists(key);
            }
        }.execute();
    }

    public Long incr(final String key)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.incr(key);
            }
        }.execute();
    }

    public Long incr(final String key, final int expire)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                Long result = jedis.incr(key);
                jedis.expire(key, expire);
                return result;
            }
        }.execute();
    }

    public Long decr(final String key)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.decr(key);
            }
        }.execute();
    }

    /**
     * 相当于list里面存放map
     *
     * <pre>
     * </pre>
     *
     * @param key
     * @return
     */
    public Long hset(final String key, final String field, final String value)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.hset(key, field, value);
            }
        }.execute();
    }

    /**
     * 通过filed取出list中的map的value值
     *
     * <pre>
     * </pre>
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(final String key, final String field)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.hget(key, field);
            }
        }.execute();
    }

    /**
     * 在list中的map的value的加减运算
     *
     * <pre>
     * </pre>
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hincrBy(final String key, final String field, final long value)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.hincrBy(key, field, value);
            }
        }.execute();
    }

    /**
     * 列出list中所有的filed
     *
     * <pre>
     * </pre>
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(final String key)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.hkeys(key);
            }
        }.execute();
    }

    /**
     * 列出list中所有filed的value
     *
     * <pre>
     * </pre>
     *
     * @param key
     * @return
     */
    public List<String> hvals(final String key)
    {
        return new Executor<List<String>>()
        {
            @Override
            List<String> action()
            {
                return jedis.hvals(key);
            }
        }.execute();
    }

    /**
     * 列出list中所有信息
     *
     * <pre>
     * </pre>
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(final String key)
    {
        return new Executor<Map<String, String>>()
        {
            @Override
            Map<String, String> action()
            {
                return jedis.hgetAll(key);
            }
        }.execute();
    }

    public Long lpush(final String key, final String... strings)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.lpush(key, strings);
            }
        }.execute();
    }

    public List<String> lrange(final String key, final long start, final long end)
    {
        return new Executor<List<String>>()
        {
            @Override
            List<String> action()
            {
                return jedis.lrange(key, start, end);
            }
        }.execute();
    }

    public Long rpush(final String key, final String... strings)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.rpush(key, strings);
            }
        }.execute();
    }

    public String lset(final String key, final long index, final String value)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.lset(key, index, value);
            }
        }.execute();
    }

    public Long lrem(final String key, final long index, final String value)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.lrem(key, index, value);
            }
        }.execute();
    }

    public String lpop(final String key)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.lpop(key);
            }
        }.execute();
    }

    public String rpop(final String key)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.rpop(key);
            }
        }.execute();
    }

    public Long llen(final String key)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.llen(key);
            }
        }.execute();
    }

    public String flushDB()
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.flushDB();
            }
        }.execute();
    }

    public Long sadd(final String key, final String... members)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.sadd(key, members);
            }
        }.execute();
    }

    public Long srem(final String key, final String... members)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.srem(key, members);
            }
        }.execute();
    }

    public Set<String> smembers(final String key)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.smembers(key);
            }
        }.execute();
    }

    public String spop(final String key)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.spop(key);
            }
        }.execute();
    }

    public Set<String> sdiff(final String... keys)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.sdiff(keys);
            }
        }.execute();
    }

    public Long sdiffstore(final String dstkey, final String... keys)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.sdiffstore(dstkey, keys);
            }
        }.execute();
    }

    public Set<String> sinter(final String... keys)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.sinter(keys);
            }
        }.execute();
    }

    public Long sinterstore(final String dstkey, final String... keys)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.sinterstore(dstkey, keys);
            }
        }.execute();
    }

    public Set<String> sunion(final String... keys)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.sunion(keys);
            }
        }.execute();
    }

    public Long scard(final String key)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.scard(key);
            }
        }.execute();
    }

    public Boolean sismember(final String key, final String member)
    {
        return new Executor<Boolean>()
        {
            @Override
            Boolean action()
            {
                return jedis.sismember(key, member);
            }
        }.execute();
    }

    public String srandmember(final String key)
    {
        return new Executor<String>()
        {
            @Override
            String action()
            {
                return jedis.srandmember(key);
            }
        }.execute();
    }

    public Long zadd(final String key, final double score, final String member)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zadd(key, score, member);
            }
        }.execute();
    }

    public Set<String> zrange(final String key, final long start, final long end)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.zrange(key, start, end);
            }
        }.execute();
    }

    public Long zrem(final String key, final String... members)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zrem(key, members);
            }
        }.execute();
    }

    public Long zrank(final String key, final String member)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zrank(key, member);
            }
        }.execute();
    }

    public Long zrevrank(final String key, final String member)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zrevrank(key, member);
            }
        }.execute();
    }

    public Set<String> zrevrange(final String key, final long start, final long end)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.zrevrange(key, start, end);
            }
        }.execute();
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min)
    {
        return new Executor<Set<String>>()
        {
            @Override
            Set<String> action()
            {
                return jedis.zrevrangeByScore(key, max, min);
            }
        }.execute();
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min)
    {
        return new Executor<Set<Tuple>>()
        {
            @Override
            Set<Tuple> action()
            {
                return jedis.zrevrangeByScoreWithScores(key, max, min);
            }
        }.execute();
    }

    public Long zcount(final String key, final double min, final double max)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zcount(key, min, max);
            }
        }.execute();
    }

    public Long zcard(final String key)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zcard(key);
            }
        }.execute();
    }

    public Double zcore(final String key, final String member)
    {
        return new Executor<Double>()
        {
            @Override
            Double action()
            {
                return jedis.zscore(key, member);
            }
        }.execute();
    }

    public Long zremrangeByRank(final String key, final long start, final long end)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zremrangeByRank(key, start, end);
            }
        }.execute();
    }

    public Long zremrangeByScore(final String key, final double start, final double end)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.zremrangeByScore(key, start, end);
            }
        }.execute();
    }

    public Long expire(final String key, final int seconds)
    {
        return new Executor<Long>()
        {
            @Override
            Long action()
            {
                return jedis.expire(key, seconds);
            }
        }.execute();
    }

    public Boolean setbit(final String key, final int offset, final String value)
    {
        return new Executor<Boolean>()
        {
            @Override
            Boolean action()
            {
                return jedis.setbit(key, offset, value);
            }
        }.execute();
    }

    public void destroy()
    {
        factory.destroy();
    }

    public void setFactory(RedisFactory factory)
    {
        this.factory = factory;
    }
}
