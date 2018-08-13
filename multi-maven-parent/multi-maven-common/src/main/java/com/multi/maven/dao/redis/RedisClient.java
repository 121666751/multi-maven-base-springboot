package com.multi.maven.dao.redis;

import com.multi.maven.context.CacheContext;
import com.multi.maven.dao.redis.bean.ZSetTuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Slf4j
public class RedisClient {

    private RedisTemplate<String, String> redisTemplate;

    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取 key 的过期时间，TTL
     *
     * @param key
     * @return
     */
    public Long getExpire(String key) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        return this.redisTemplate.getExpire(key);
    }

    /////////////////////////////////////////////////////////////////////////////
    //                             Strings                                     //
    /////////////////////////////////////////////////////////////////////////////

    /**
     * 添加string元素
     * ignoreWhenExist==false:如果key存在，它将被重写。
     * ignoreWhenExist==true:当且仅当 key 不存在时添加，若给定的 key 已经存在，则 不做任何动作。
     *
     * @param key
     * @param value
     * @param ignoreWhenExist
     * @return
     */
    public boolean addString(final String key, final String value, boolean ignoreWhenExist) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        log.debug("redis addString,param:key[{}],value[{}],ignoreWhenExist[{}]", key, value, ignoreWhenExist);
        boolean resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] values = serializer.serialize(value);
                if (ignoreWhenExist) {
                    return connection.setNX(keys, values);
                }
                connection.set(keys, values);
                return true;
            }
        });
        return resultBoolean;
    }

    /**
     * 添加string元素，同时指定过期时间
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public boolean addStringWithExpire(final String key, long seconds, final String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        log.debug("redis addStringWithExpire,param:key[{}],seconds[{}],value[{}]", key, seconds, value);
        boolean resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] values = serializer.serialize(value);
                connection.setEx(keys, seconds, values);
                return true;
            }
        });
        return resultBoolean;
    }

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        log.debug("redis getString,param:key[{}]", key);
        String resultStr = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] values = connection.get(keys);
                if (values == null) {
                    return null;
                }
                String value = serializer.deserialize(values);
                return value;
            }
        });
        return resultStr;
    }

    /**
     * 将 key 中储存的数字值增一。如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 此 操作。
     *
     * @param key
     * @return
     */
    public long incrementAndGet(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        log.debug("redis incrementAndGet,param:key[{}]", key);
        long currentCount = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                return connection.incr(keys);
            }
        });
        return currentCount;
    }

    /**
     * 将 key 中储存的数字值增加inc。如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 此 操作。
     *
     * @param key
     * @return
     */
    public double incrementAndGet(final String key, final double inc) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        log.debug("redis incrementAndGet,param:key[{}],inc[{}]", key, inc);
        double currentCount = redisTemplate.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                return connection.incrBy(keys, inc);
            }
        });
        return currentCount;
    }

    /**
     * set新值，返回旧值
     *
     * @param key
     * @param value
     * @return
     */
    public String getAndSet(final String key, final String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        log.debug("redis getAndSet,param:key[{}],value[{}]", key, value);
        String oldValue = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] values = serializer.serialize(value);
                byte[] oldValues = connection.getSet(keys, values);
                if (oldValues == null) {
                    return null;
                }
                String oldValue = serializer.deserialize(oldValues);
                return oldValue;
            }
        });
        return oldValue;
    }


    /////////////////////////////////////////////////////////////////////////////
    //                                keys                                     //
    /////////////////////////////////////////////////////////////////////////////

    /**
     * 删除key
     *
     * @param keys
     * @return 删除个数
     */
    public long deleteKey(final String... keys) {
        Assert.notEmpty(keys);
        log.debug("redis deleteKey,param:keys[{}]", StringUtils.join(keys, ","));
        long delCount = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.del(stringArrayToByteArrays(keys));
            }
        });
        CacheContext.deleteKey(keys);
        return delCount;
    }


    /**
     * 查找Key
     *
     * @param pattern
     * @return 返回匹配的key集合
     */
    public List<String> findKeys(final String pattern) {
        Assert.notNull(pattern);
        log.debug("redis findKeys,param:pattern[{}]", pattern);
        List<String> list = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] patternBytes = getRedisSerializer().serialize(pattern);
                Set<byte[]> values = connection.keys(patternBytes);
                return BytesSetToStringList(values);
            }
        });
        return list;
    }

    /**
     * 返回key是否存在。
     *
     * @param key
     * @return
     */
    public boolean existsKey(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        log.debug("redis existsKey,param:key[{}]", key);
        boolean exists = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                return connection.exists(keys);
            }
        });
        return exists;
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public boolean expireKey(final String key, long seconds) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        log.debug("redis expireKey,param:key[{}],seconds[{}]", key, seconds);
        boolean exists = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                return connection.expire(keys, seconds);
            }
        });
        return exists;
    }


    /**
     * key
     * Find all keys matching the given pattern.
     * 根据指定的pattern匹配所有的key
     * Examples ：
     * h?llo matches hello, hallo and hxllo
     * h*llo matches hllo and heeeello
     * h[ae]llo matches hello and hallo, but not hillo
     *
     * @param pattern 键
     * @return 返回所有匹配的key的List集合
     * @see // http://redis.io/commands/keys
     */
    public List<String> keys(final String pattern) {
        Assert.hasText(pattern);
        log.debug("redis keys,param:pattern[{}]", pattern);
        List<String> list = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] patternSer = getRedisSerializer().serialize(pattern);
                return BytesSetToStringList(connection.keys(patternSer));
            }
        });
        return list;
    }


    //////////////////////////////////////////////////////////
    //                       hashes                         //
    //////////////////////////////////////////////////////////

    /**
     * 设置 key 指定的哈希集中指定字段的值。该命令将重写所有在哈希集中存在的字段。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联
     *
     * @param key
     * @param valueMap
     * @return
     */
    public boolean addToHash(final String key, final Map<String, ?> valueMap) {
        Assert.notNull(key, "this key is required; it must not be null");
        Assert.notEmpty(valueMap, "this valueMap must not be empty; it must contain at least one entry");
        log.debug("redis addToHash,param:key[{}],valueMap[{}]", key, valueMap);
        boolean resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                connection.hMSet(keys, transformBytesMap(valueMap));
                return true;
            }
        });
        return resultBoolean;
    }

    /**
     * 更新Hash中的指定field
     * </p>
     * 当Hash不存在时，不执行hMset操作
     *
     * @param key      Hash Key
     * @param valueMap key为Hash的field，value为Hash value
     * @return
     */
    public boolean updateHash(final String key, final Map<String, ?> valueMap) {
        Assert.notNull(key, "this key is required; it must not be null");
        Assert.notEmpty(valueMap, "this valueMap must not be empty; it must contain at least one entry");
        log.debug("redis updateHash,param:key[{}],valueMap[{}]", key, valueMap);
        boolean resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                if (connection.exists(keys)) {
                    connection.hMSet(keys, transformBytesMap(valueMap));
                }
                return true;
            }
        });
        return resultBoolean;
    }

    /**
     * 设置 key 指定的哈希集中指定字段的值。
     * 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联。
     * ignoreWhenExist==false:如果字段在哈希集中存在，它将被重写。
     * ignoreWhenExist==true:只在 key 指定的哈希集中不存在指定的字段时，设置字段的值.如果字段已存在，该操作无效果。
     *
     * @param key
     * @param field           字段
     * @param value
     * @param ignoreWhenExist field已存在时忽略操作
     * @return
     */
    public boolean addToHash(final String key, final String field, final String value, final boolean ignoreWhenExist) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            throw new NullPointerException();
        }
        log.debug("redis addToHash,param:key[{}],field[{}],value[{}],ignoreWhenExist[{}]", key, field, value, ignoreWhenExist);
        boolean resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] fields = serializer.serialize(field);
                byte[] values = serializer.serialize(value == null ? "" : value);
                if (ignoreWhenExist) {
                    return connection.hSetNX(keys, fields, values);
                }
                return connection.hSet(keys, fields, values);
            }
        });
        return resultBoolean;
    }

    /**
     * 返回 key 指定的哈希集中指定字段的值。
     * 对于哈希集中不存在的每个字段，返回 nil 值。因为不存在的keys被认为是一个空的哈希集，对一个不存在的 key 执行 HMGET 将返回一个只含有 nil 值的列表
     *
     * @param key
     * @param fields
     * @return 含有给定字段及其值的列表，并保持与请求相同的顺序。
     */
    public Map<String, String> getHash(final String key, final String... fields) {
        Assert.notNull(key);
        log.debug("redis getHash,param:key[{}],fields[{}]", key, StringUtils.join(fields, ","));
        byte[] keySer = getRedisSerializer().serialize(key);

        if (ArrayUtils.isEmpty(fields)) {
            Map<String, String> map = redisTemplate.execute(new RedisCallback<Map<String, String>>() {
                @Override
                public Map<String, String> doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    return bytesMapToStringsMap(connection.hGetAll(keySer));
                }
            });
            return map;
        } else {
            List<byte[]> result = redisTemplate.execute(new RedisCallback<List<byte[]>>() {
                @Override
                public List<byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[][] bs = stringArrayToByteArrays(fields);
                    List<byte[]> list = connection.hMGet(keySer, bs);
                    if (CollectionUtils.isEmpty(list)) {
                        return null;
                    }
                    return list;
                }
            });
            if (CollectionUtils.isEmpty(result) || this.allElementNull(result)) {
                return null;
            }
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < fields.length; i++) {
                map.put(fields[i], getRedisSerializer().deserialize(result.get(i)));
            }
            return map;

        }

    }

    private <T> boolean allElementNull(List<T> list) {
        if (list == null) {
            return true;
        }
        for (T t : list) {
            if (t != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 增加 key 指定的哈希集中指定字段的数值。如果 key 不存在，会创建一个新的哈希集并与 key 关联。如果字段不存在，则字段的值在该操作执行前被设置为 0
     * HINCRBY 支持的值的范围限定在 64位 有符号整数
     *
     * @param key
     * @return 增值操作执行后的该字段的值。
     */
    public long incrementAndGetToHash(final String key, final String field, long delta) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(field)) {
            throw new NullPointerException();
        }
        log.debug("redis incrementAndGetToHash,param:key[{}],field[{}],delta[{}]", key, field, delta);
        long currentCount = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] fields = serializer.serialize(field);
                return connection.hIncrBy(keys, fields, delta);
            }
        });
        return currentCount;
    }


    /**
     * Hash
     * Delete given hash fields.
     * HDEL key field [field ...]
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key    键
     * @param fields 属性集合
     * @return 被成功移除的域的数量，不包括被忽略的域。
     * key不存在，返回0
     * @see // http://redis.io/commands/hdel
     */
    public long deleteHashFields(final String key, final String... fields) {
        Assert.hasText(key, "this key must have text; it must not be null, empty, or blank");
        Assert.notEmpty(fields, "this fields must not be empty: it must contain at least 1 element");
        log.debug("redis deleteHashFields,param:key[{}],fields[{}]", key, StringUtils.join(fields, ","));
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[][] bs = stringArrayToByteArrays(fields);
                return connection.hDel(keys, bs);
            }
        });
        return result;
    }

    //////////////////////////////////////////////////////////
    //                        sets                          //
    //////////////////////////////////////////////////////////

    /**
     * 添加一个或多个指定的member元素到集合的 key中.指定的一个或者多个元素member 如果已经在集合key中存在则忽略.
     * 如果集合key 不存在，则新建集合key,并添加member元素到集合key中.
     * 如果key 的类型不是集合则返回错误.
     *
     * @param key
     * @param values
     * @return 返回新成功添加到集合里元素的数量，不包括已经存在于集合中的元素.
     */
    public long addToSet(final String key, final String... values) {
        if (StringUtils.isBlank(key) || values == null) {
            throw new NullPointerException();
        }
        log.debug("redis addToSet,param:key[{}],values[{}]", key, StringUtils.join(values, ","));
        long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[][] bs = stringArrayToByteArrays(values);
                return connection.sAdd(keys, bs);
            }
        });
        return count;
    }

    /**
     * 添加一个或多个指定的member元素到集合的 key中.指定的一个或者多个元素member 如果已经在集合key中存在则忽略.
     * 如果集合key 不存在，则新建集合key,并添加member元素到集合key中.
     * 如果key 的类型不是集合则返回错误.
     *
     * @param key
     * @param values
     * @return 返回新成功添加到集合里元素的数量，不包括已经存在于集合中的元素.
     */
    public long addToSet(final String key, final Set<String> values) {
        if (StringUtils.isBlank(key) || values == null) {
            throw new NullPointerException();
        }
        log.debug("redis addToSet,param:key[{}],values[{}]", key, values);
        long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[][] bs = StringSetToByteArrays(values);
                return connection.sAdd(keys, bs);
            }
        });
        return count;
    }


    /**
     * 返回指定多个集合的差集的元素
     *
     * @param key
     * @return
     */
    public Set<String> getSetDiff(final String... key) {
        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis getSetDiff,param:key[{}]", StringUtils.join(key, ","));
        Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[][] keys = stringArrayToByteArrays(key);
                Set<byte[]> set = connection.sDiff(keys);
                if (set == null || set.isEmpty()) {
                    return null;
                }
                Set<String> returnSet = new HashSet<String>();
                for (byte[] b : set) {
                    returnSet.add(serializer.deserialize(b));
                }
                return returnSet;
            }
        });
        return result;
    }

    /**
     * Set
     * Diff all sets for given keys and store result in destKey
     * <p>
     * SDIFFSTORE destination key [key ...]
     * 这个命令的作用和 SDIFF 类似，但它将结果保存到 destKey 集合，而不是简单地返回结果集。
     * 如果 destKey 集合已经存在，则将其覆盖。
     * destKey 可以是 key 本身。
     *
     * @param destKey 结果集合
     * @param keys    一个或多个待操作的集合
     * @return 结果集中的元素数量。
     * 参数destKey为null时，返回-1
     * 参数keys为null时，或者长度为0时，返回-1
     * @see // http://redis.io/commands/sdiffstore
     */
    public Long getSetDiffStore(final String destKey, final String... keys) {
        Assert.hasText(destKey, "this destKey must have text; it must not be null, empty, or blank");
        Assert.notEmpty(keys, "this keys must not be empty: it must contain at least 1 element");
        log.debug("redis getSetDiffStore,param:destKey[{}],key[{}]", destKey, StringUtils.join(keys, ","));
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();

                byte[] destKeySer = serializer.serialize(destKey);
                byte[][] keysBytes = stringArrayToByteArrays(keys);

                return connection.sDiffStore(destKeySer, keysBytes);
            }
        });
    }


    /**
     * 返回指定所有的集合的成员的交集
     *
     * @param key
     * @return
     */
    public Set<String> getSetInter(final String... key) {
        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis getSetInter,param:key[{}]", StringUtils.join(key, ","));
        Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[][] keys = stringArrayToByteArrays(key);
                Set<byte[]> set = connection.sInter(keys);
                if (set == null || set.isEmpty()) {
                    return null;
                }
                Set<String> returnSet = new HashSet<String>();
                for (byte[] b : set) {
                    returnSet.add(serializer.deserialize(b));
                }
                return returnSet;
            }
        });
        return result;
    }

    /**
     * 返回给定的多个集合的并集中的所有成员.
     *
     * @param key
     * @return
     */
    public Set<String> getSetUnion(final String... key) {
        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis getSetUnion,param:key[{}]", StringUtils.join(key, ","));
        Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[][] keys = stringArrayToByteArrays(key);
                Set<byte[]> set = connection.sUnion(keys);
                if (set == null || set.isEmpty()) {
                    return null;
                }
                Set<String> returnSet = new HashSet<String>();
                for (byte[] b : set) {
                    returnSet.add(serializer.deserialize(b));
                }
                return returnSet;
            }
        });
        return result;
    }


    /**
     * Set
     * Union sorted sets and store result in destination key.
     * <p>
     * SUNIONSTORE destKey numkeys key [key ...]
     * <p>
     * 计算给定的一个或多个有序集的并集，其中给定 key 的数量必须以 numkeys 参数指定，并将该并集(结果集)储存到 destKey 。
     * 默认情况下，结果集中某个成员的 score 值是所有给定集下该成员 score 值之 和 。
     *
     * @param destKey 目标键
     * @param setKeys Set key数组
     * @return 保存到 destKey 的结果集的Size
     * @see // http://redis.io/commands/sunionstore
     */
    public Long getSetUnionStore(final String destKey, final String... setKeys) {

        Assert.hasText(destKey, "this destKey must have text; it must not be null, empty, or blank");
        Assert.notEmpty(setKeys, "this setKeys must not be empty: it must contain at least 1 element");

        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] destKeySer = getRedisSerializer().serialize(destKey);
                byte[][] keys = stringArrayToByteArrays(setKeys);

                return connection.sUnionStore(destKeySer, keys);
            }
        });
    }

    /**
     * 返回key集合所有的元素.
     *
     * @param key
     * @return
     */
    public Set<String> getSet(final String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis getSet,param:key[{}]", key);
        Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                Set<byte[]> set = connection.sMembers(keys);
                if (set == null || set.isEmpty()) {
                    return null;
                }
                Set<String> returnSet = new HashSet<String>();
                for (byte[] b : set) {
                    returnSet.add(serializer.deserialize(b));
                }
                return returnSet;
            }
        });
        return result;
    }

    /**
     * 返回成员 member 是否是存储的集合 key的成员.
     *
     * @param key
     * @param member
     * @return
     */
    public boolean isSetMember(final String key, final String member) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(member)) {
            throw new NullPointerException();
        }
        log.debug("redis isSetMember,param:key[{}],member[{}]", key, member);
        boolean resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] values = serializer.serialize(member);
                return connection.sIsMember(keys, values);
            }
        });
        return resultBoolean;
    }

    /**
     * 在key集合中移除指定的元素. 如果指定的元素不是key集合中的元素则忽略 如果key集合不存在则被视为一个空的集合，该命令返回0.
     * 如果key的类型不是一个集合,则返回错误.
     *
     * @param key
     * @param value
     * @return
     */
    public long deleteSetMember(final String key, final String... value) {
        if (StringUtils.isBlank(key) || value == null) {
            throw new NullPointerException();
        }
        log.debug("redis deleteSetMember,param:key[{}],value[{}]", key, StringUtils.join(value, ","));
        long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[][] bs = stringArrayToByteArrays(value);
                return connection.sRem(keys, bs);
            }
        });
        return count;
    }

    /**
     * 返回set中指定count的随机元素列表
     * 如果count是整数且小于元素的个数，返回含有 count 个不同的元素的数组,
     * 如果count是个整数且大于集合中元素的个数时,仅返回整个集合的所有元素,
     * 当count是负数,则会返回一个包含count的绝对值的个数元素的数组，
     * 如果count的绝对值大于元素的个数,则返回的结果集里会出现一个元素出现多次的情况.
     *
     * @param key
     * @param count
     * @return
     */
    public List<String> getSetRandomMember(final String key, long count) {
        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis getSetRandomMember,param:key[{}],count[{}]", key, count);
        List<String> result = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                List<byte[]> list = connection.sRandMember(keys, count);
                if (list == null || list.isEmpty()) {
                    return null;
                }
                List<String> returnList = new ArrayList<String>(list.size());
                for (byte[] b : list) {
                    returnList.add(serializer.deserialize(b));
                }
                return returnList;
            }
        });
        return result;
    }


    /**
     * Set
     * Get size of set at key.
     * <p>
     * 返回集合 key 的基数(集合中元素的数量)。
     *
     * @param key 键
     * @return 集合的基数。
     * 当 key 不存在时，返回 0 。
     * @see //http://redis.io/commands/scard
     */
    public Long countSet(final String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis countSet,param:key[{}]", key);
        Long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                return connection.sCard(keys);
            }
        });
        return count;
    }

    /**
     * Set
     * Remove and return a random member from set at key.
     * <p>
     * 移除并返回集合中的一个随机元素。
     * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
     *
     * @param key 键
     * @return 被移除的随机元素。
     * 当 key 不存在或 key 是空集时，返回 null
     * 当参数key为null时，返回null
     * @see //http://redis.io/commands/spop
     */
    public String getAndRemoveFromSet(final String key) {

        if (key == null) {
            throw new NullPointerException();
        }
        log.debug("redis getAndRemoveFromSet,param:key[{}]", key);
        String value = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keys = serializer.serialize(key);
                byte[] values = connection.sPop(keys);
                if (values == null) {
                    return null;
                }
                String value = serializer.deserialize(values);
                return value;
            }
        });
        return value;
    }

    //////////////////////////////////////////////////////////
    //                        zsets                         //
    //////////////////////////////////////////////////////////

    /**
     * ZSet
     * Add value to a sorted set at key, or update its score if it already exists.
     * <p>
     * ZADD key score member [[score member] [score member] ...]
     * 将一个 member 元素及其 score 值加入到有序集 key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     * score 值可以是整数值或双精度浮点数。
     * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key   键
     * @param score 序号
     * @param value 值，当值为null，将转换为""
     * @return 成功返回true  失败返回false
     * @see //http://redis.io/commands/zadd
     */
    public boolean addToZSet(final String key, final double score, final String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new NullPointerException();
        }
        log.debug("redis addToZSet,param:key[{}],score[{}],value[{}]", key, score, value);
        Boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keySer = serializer.serialize(key);
                byte[] valueSer = serializer.serialize(value);
                return connection.zAdd(keySer, score, valueSer);
            }
        });
        return result;
    }

    /**
     * ZSet
     * Add tuples to a sorted set at key, or update its score if it already exists.
     * <p>
     * ZADD key score member [[score member] [score member] ...]
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，
     * 并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     * score 值可以是整数值或双精度浮点数。
     * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key    键
     * @param tuples 存储score和value的集合
     * @return 返回成功的数量
     * @see // http://redis.io/commands/zadd
     */
    public Long addToZSet(final String key, final Set<Tuple> tuples) {
        Assert.notNull(key, "this key is required; it must not be null");
        Assert.notNull(tuples, "this tuples is required; it must not be null");

        log.debug("redis addToZSet,param:key[{}],tuples[{}]", key, tuples);
        Long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = getRedisSerializer().serialize(key);
                return connection.zAdd(keySer, tuples);
            }
        });
        return count;
    }


    /**
     * ZSet
     * Get the size of sorted set with key.
     * <p>
     * ZCARD key
     * 返回有序集 key 的Size
     *
     * @param key 键
     * @return 当 key 存在且是有序集类型时，返回有序集的基数。
     * 当 key 不存在时，返回 0 。
     * 当参数key为null时，返回-1
     * @see // http://redis.io/commands/zcard
     */
    public long countZSet(final String key) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException();
        }
        log.debug("redis countZSet,param:key[{}]", key);
        Long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keySer = serializer.serialize(key);
                return connection.zCard(keySer);
            }
        });
        return count;
    }

    /**
     * ZSet
     * Get elements between begin and end from sorted set.
     * <p>
     * ZRANGE key start stop [WITHSCORES]
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     * <p>
     * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
     * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。
     * 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。
     * 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。
     *
     * @param key   键
     * @param begin 开始偏移量
     * @param end   结束偏移量
     * @return 返回结果集
     * @see //http://redis.io/commands/zrange
     */
    public List<String> getZsetRange(final String key, final long begin, final long end) {

        if (StringUtils.isBlank(key))
            throw new NullPointerException();
        log.debug("redis getZsetRange,param:key[{}],begin[{}],end[{}]", key, begin, end);
        List<String> list = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[] keySer = serializer.serialize(key);
                return BytesSetToStringList(connection.zRange(keySer, begin, end));
            }
        });
        return list;
    }

    /**
     * ZSet
     * Determine the index of element with value in a sorted set.
     * <p>
     * ZRANK key value
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
     * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
     * 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
     *
     * @param key   键，不能为空
     * @param value 值，不能为空
     * @return 返回value的排名
     * @see // http://redis.io/commands/zrank
     */
    public Long getZsetRank(final String key, final String value) {
        Assert.notNull(key, "this key is required; it must not be null");
        Assert.notNull(value, "this value is required; it must not be null");
        log.debug("redis getZsetRank,param:key[{}],value[{}]", key, value);
        Long rank = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();

                byte[] keySer = serializer.serialize(key);
                byte[] valueSer = serializer.serialize(value);

                return connection.zRank(keySer, valueSer);
            }
        });
        return rank;
    }


    /**
     * ZSet
     * Get the score of element with value from sorted set with key key.
     * <p>
     * ZSCORE key value
     * 返回有序集 key 中，成员 value 的 score 值。
     * 如果 value 元素不是有序集 key 的成员，或 key 不存在，返回 null 。
     *
     * @param key   键
     * @param value 值
     * @return value 成员的 score 值
     * @see // http://redis.io/commands/zscore
     */
    public Double getZSetScore(final String key, final String value) {
        Assert.hasText(key, "this key must have text; it must not be null, empty, or blank");
        Assert.notNull(value, "this value is required; it must not be null");
        log.debug("redis getZSetScore,param:key[{}],value[{}]", key, value);
        Double score = redisTemplate.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = getRedisSerializer().serialize(key);
                byte[] valueSer = getRedisSerializer().serialize(value);

                return connection.zScore(keySer, valueSer);
            }
        });
        return score;
    }


    /**
     * ZSet
     * Get elements where score is between min and max from sorted set ordered from high to low.
     * <p>
     * ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
     *
     * @param key 键
     * @param min 起始score
     * @return 有序集成员的列表。
     * 当参数key为null，抛出NullPointerException异常
     * @parram max 终止score
     * @see // http://redis.io/commands/zrevrange
     */
    public List<String> getZRevRangeByScore(final String key, final double min, final double max, final long offset, final long count) {

        Assert.hasText(key);
        log.debug("redis getZRevRangeByScore,param:key[{}],min[{}],max[{}],offset[{}],count[{}]", key, min, max, offset, count);
        List<String> list = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keys = getRedisSerializer().serialize(key);
                return BytesSetToStringList(connection.zRevRangeByScore(keys, min, max, offset, count));
            }
        });
        return list;
    }


    /**
     * ZSet
     * Get set of RedisZSetCommands.Tuples in range from begin to end where score is between min and max from sorted set.
     * <p>
     * ZRANGEBYSCORE key min max  [LIMIT offset count]
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
     * 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
     *
     * @param key    键
     * @param min    开始序号
     * @param max    结束序号
     * @param offset 偏移量
     * @param count  数量
     * @return 返回结果集
     * 当参数key为null时，返回null
     * @see // http://redis.io/commands/zrangebyscore
     */
    public List<ZSetTuple> getZRangeByScoreWithScores(final String key, final double min, final double max, final long offset, final long count) {

        Assert.hasText(key);

        return redisTemplate.execute(new RedisCallback<List<ZSetTuple>>() {
            @Override
            public List<ZSetTuple> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = redisTemplate.getStringSerializer().serialize(key);
                Set<Tuple> sets = connection.zRangeByScoreWithScores(keySer, min, max, offset, count);
                if (sets == null || sets.size() == 0) {
                    return null;
                }
                List<ZSetTuple> result = new ArrayList<ZSetTuple>(sets.size());

                Iterator<Tuple> it = sets.iterator();
                while (it.hasNext()) {
                    Tuple tuple = it.next();
                    result.add(new ZSetTuple(tuple.getValue(), tuple.getScore()));
                }
                return result;
            }
        });
    }


    //////////////////////////////////////////////////////////
    //                        list                          //
    //////////////////////////////////////////////////////////

    /**
     * List
     * Prepend values to key.
     * <p>
     * LPUSH key value [value ...]
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头：
     * 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
     * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * <p>
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    键
     * @param values 一个或多个value,当value为null时，将转换为""
     * @return 执行 LPUSH 命令后，列表的长度。
     * @see //http://redis.io/commands/lpush
     */
    public Long addToListL(final String key, final String... values) {
        Assert.hasText(key, "this key must have text; it must not be null, empty, or blank");
        Assert.notEmpty(values, "this values must not be empty: it must contain at least 1 element");
        return (Long) redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[][] bs = stringArrayToByteArrays(values);

                return connection.lPush(serializer.serialize(key), bs);
            }
        });
    }


    /**
     * List
     * Append values to key.
     * <p>
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：
     * 比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
     * <p>
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    键
     * @param values 值集合
     * @return 执行 RPUSH 操作后，表的长度。
     * @see //http://redis.io/commands/rpush
     */
    public Long addToListR(final String key, final String... values) {
        Assert.hasText(key, "this key must have text; it must not be null, empty, or blank");
        Assert.notEmpty(values, "this values must not be empty: it must contain at least 1 element");

        return (Long) redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = getRedisSerializer();
                byte[][] bs = stringArrayToByteArrays(values);
                return connection.rPush(serializer.serialize(key), bs);
            }
        });
    }


    /**
     * List
     * <p>
     * 类似于乐观锁，执行lpush时，监控key是否被改变，如果改变，放弃操作
     * <p>
     * 先删除key，再将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：
     * 比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
     * <p>
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    键
     * @param values 值集合
     * @return 执行 RPUSH 操作后，表的长度。
     * @see //http://redis.io/commands/rpush
     */
    public void clearOnAddToListR(final String key, final List<?> values) {
        Assert.notNull(key);
        log.debug("redis clearOnAddToListR,param:key[{}],values[{}]", key, values);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = getRedisSerializer().serialize(key);
                connection.watch(keySer);
                connection.multi();
                connection.del(keySer);
                connection.rPush(keySer, objectListToByteArrays(values));
                connection.exec();
                return null;
            }
        });
    }

    /**
     * 先从List中删除value值，再讲value值push到List头部
     *
     * @param key
     * @param value
     */
    public void deleteOnAddToListL(final String key, final String value) {
        Assert.notNull(key, "this key is required; it must not be null");
        Assert.notNull(value, "this value is required; it must not be null");
        log.debug("redis deleteOnAddToListL,param:key[{}],values[{}]", key, value);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = getRedisSerializer().serialize(key);
                byte[] valueSer = getRedisSerializer().serialize(value);
                connection.watch(keySer);
                connection.multi();
                connection.lRem(keySer, 0, valueSer);
                connection.lPush(keySer, valueSer);
                connection.exec();
                return null;
            }
        });
    }


    /**
     * List
     * Removes the first count occurrences of value from the list stored at key.
     * <p>
     * LREM key count value
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
     * <p>
     * count 的值可以是以下几种：
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
     * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
     * count = 0 : 移除表中所有与 value 相等的值。
     *
     * @param key   键
     * @param count 删除数量
     * @param value 待删除的值 value为null时，将转换为""
     * @return 被移除元素的数量。
     * 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时， LREM 命令总是返回 0 。
     * @see// http://redis.io/commands/lrem
     */
    public Long deleteListValue(final String key, final long count, final String value) {
        Assert.notNull(key, "this key is required; it must not be null");
        log.debug("redis deleteListValue,param:key[{}],count[{}],value[{}]", key, count, value);
        Long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = getRedisSerializer().serialize(key);
                byte[] valueSer = getRedisSerializer().serialize(value);
                return connection.lRem(keySer, count, valueSer);
            }
        });
        return result;
    }


    /**
     * List
     * Get elements between begin and end from list at key.
     * <p>
     * LRANGE key begin end
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * <p>
     * 注意LRANGE命令和编程语言区间函数的区别
     * 假如你有一个包含一百个元素的列表，对该列表执行 LRANGE list 0 10 ，结果是一个包含11个元素的列表，
     * 这表明 end 下标也在 LRANGE 命令的取值范围之内(闭区间)，
     * 这和某些语言的区间函数可能不一致，比如Ruby的 Range.new 、 Array#slice 和Python的 range() 函数。
     * <p>
     * 超出范围的下标
     * 超出范围的下标值不会引起错误。
     * 如果 begin 下标比列表的最大下标  ( LLEN list 减去 1 )还要大，那么 LRANGE 返回一个空列表。
     * <p>
     * 如果 end 下标比 最大下标还要大，Redis将 stop 的值设置为 最大下标 。
     *
     * @param key   键
     * @param begin 起始下标
     * @param end   终止下标
     * @return 一个列表，包含指定区间内的元素。
     * @see //http://redis.io/commands/lrange
     */
    public List<String> getList(final String key, final long begin, final long end) {
        Assert.notNull(key);
        log.debug("redis getList,param:key[{}],begin[{}],end[{}]", key, begin, end);
        List<String> list = redisTemplate.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keySer = getRedisSerializer().serialize(key);
                List<byte[]> list = connection.lRange(keySer, begin, end);

                return byteListToStringList(list);
            }
        });
        return list;
    }


    /**
     * List
     * Trim list at key to elements between begin and end.
     * <p>
     * LTRIM key start stop
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 当 key 不是列表类型时，返回一个错误。
     * <p>
     * 假如你有一个包含一百个元素的列表 list ，对该列表执行 LTRIM list 0 10 ，结果是一个包含11个元素的列表，这表明 stop 下标也在 LTRIM 命令的取值范围之内(闭区间)
     * <p>
     * 超出范围的下标
     * 超出范围的下标值不会引起错误。
     * <p>
     * <p>
     * LTRIM 命令通常和 LPUSH 命令或 RPUSH 命令配合使用，举个例子：
     * LPUSH log newest_log
     * LTRIM log 0 99
     *
     * @param key   键
     * @param begin 开始下标
     * @param end   结束下标
     * @return 成功返回true  失败返回false
     * @see //http://redis.io/commands/ltrim
     */
    public void trimList(final String key, final long begin, final long end) {
        Assert.notNull(key, "this key is required; it must not be null");
        log.debug("redis trimList,param:key[{}],begin[{}],end[{}]", key, begin, end);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                byte[] keys = getRedisSerializer().serialize(key);
                connection.lTrim(keys, begin, end);
                return null;
            }
        });
    }

    private byte[][] objectListToByteArrays(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        byte[][] bs = null;
        for (Object obj : list) {
            String value = (obj == null ? "" : String.valueOf(obj));
            byte[] field = getRedisSerializer().serialize(value);
            bs = ArrayUtils.add(bs, field);
        }
        return bs;
    }


    private List<String> byteListToStringList(List<byte[]> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        for (byte[] b : list) {
            result.add(getRedisSerializer().deserialize(b));
        }

        return result;
    }

    private byte[][] stringArrayToByteArrays(String[] strings) {
        byte[][] bs = null;
        for (String s : strings) {
            byte[] field = getRedisSerializer().serialize(s);
            bs = ArrayUtils.add(bs, field);
        }
        return bs;
    }

    private byte[][] StringSetToByteArrays(Set<String> set) {
        byte[][] bs = null;
        for (String s : set) {
            byte[] field = getRedisSerializer().serialize(s);
            bs = ArrayUtils.add(bs, field);
        }
        return bs;
    }

    private List<String> BytesSetToStringList(Set<byte[]> set) {
        List<String> list = new ArrayList<String>(set.size());
        Iterator<byte[]> it = set.iterator();
        while (it.hasNext()) {
            String value = getRedisSerializer().deserialize(it.next());
            list.add(value);
        }
        return list;
    }

    private Map<byte[], byte[]> transformBytesMap(Map<String, ?> map) {
        Map<byte[], byte[]> targetMap = new HashMap<byte[], byte[]>();
        if (map == null) {
            return targetMap;
        }
        for (Entry<String, ?> e : map.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            targetMap.put(
                    getRedisSerializer().serialize(e.getKey()),
                    getRedisSerializer().serialize(
                            String.valueOf(e.getValue())));
        }
        return targetMap;
    }

    /**
     * Map<byte[],byte[]>  转 Map<String,String>
     *
     * @param map
     * @return
     */
    private Map<String, String> bytesMapToStringsMap(Map<byte[], byte[]> map) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        Iterator<Entry<byte[], byte[]>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<byte[], byte[]> entry = it.next();
            result.put(getRedisSerializer().deserialize(entry.getKey()),
                    getRedisSerializer().deserialize(entry.getValue()));
        }

        return result;
    }


}
