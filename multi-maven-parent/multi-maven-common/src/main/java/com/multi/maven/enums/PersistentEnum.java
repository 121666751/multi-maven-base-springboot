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
public interface PersistentEnum<T> {
    T getValue();

    String getDisplayName();
}
