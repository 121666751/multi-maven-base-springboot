package com.multi.maven.enums;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public interface CacheKeyEnum<T> extends PersistentEnum<T>{
        String getKey(String... var1);
}
