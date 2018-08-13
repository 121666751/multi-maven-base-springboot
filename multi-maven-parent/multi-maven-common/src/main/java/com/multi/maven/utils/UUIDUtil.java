package com.multi.maven.utils;



import org.bson.types.ObjectId;

import java.util.UUID;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public class UUIDUtil {

    public static String getJavaUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getObjectId() {
        return ObjectId.get().toHexString();
    }

}