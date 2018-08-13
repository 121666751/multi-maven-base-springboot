package com.multi.maven.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: litao
 * @see:
 * @description: 基于LinkedHashMap实现LRU缓存调度算法原理及应用
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */

public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = -7877165224399717111L;
    private static final int LRU_MAX_CAPACITY = 1024;
    private int capacity;

    public LRULinkedHashMap() {
    }

    public LRULinkedHashMap(int initialCapacity, float loadFactor, boolean isLRU) {
        super(initialCapacity, loadFactor, true);
        this.capacity = 1024;
    }

    public LRULinkedHashMap(int initialCapacity, float loadFactor, boolean isLRU, int lruCapacity) {
        super(initialCapacity, loadFactor, true);
        this.capacity = lruCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.capacity;
    }
}
