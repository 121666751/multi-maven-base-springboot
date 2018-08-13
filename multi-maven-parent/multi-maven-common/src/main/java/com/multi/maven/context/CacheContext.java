package com.multi.maven.context;

import com.multi.maven.utils.LRULinkedHashMap;
import com.multi.maven.utils.LogTraceUtil;
import com.multi.maven.utils.LogTraceUtil.LogTrace;
import com.multi.maven.utils.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CacheContext {

    private static LRULinkedHashMap<String, List<String>> context = new LRULinkedHashMap<String, List<String>>();

    //过滤key关键字,先定义成常量
    private static final String[] DELETEKEYFILTER = PropertyUtil.getProperty("auto.delete.keys", "").split(";");

    public static void deleteKey(String... keys) {
        if(keys==null||keys.length<=0) {
            return;
        }
        boolean isDeleteKey = false;
        for(String filter:DELETEKEYFILTER) {
            if(StringUtils.startsWith(keys[0], filter)) {
                isDeleteKey = true;
                break;
            }
        }
        if(!isDeleteKey) {
            return;
        }
        String uuid = LogTraceUtil.getLogTrace(LogTrace.UUID);
        if(StringUtils.isBlank(uuid)) {
            return;
        }
        List<String> list = context.get(uuid);
        if(list==null) {
            list = new ArrayList<String>();
            for(String key:keys) {
                list.add(key);
            }
            List<String> tempList = context.putIfAbsent(uuid, list);
            if(tempList != null) {
                for(String key:keys) {
                    tempList.add(key);
                }
            }
        }else {
            for(String key:keys) {
                if(!list.contains(key)) {
                    list.add(key);
                }
            }
        }
    }

    public static List<String> getDeleteKeyList(){
        String uuid = LogTraceUtil.getLogTrace(LogTrace.UUID);
        if(StringUtils.isBlank(uuid)) {
            return null;
        }
        List<String> list = context.get(uuid);
        boolean removeFlag = context.remove(uuid, list);
        if(!removeFlag) {
            return null;
        }
        return list;
    }

    public static void deleteKeyById(String id,String... keys) {
        if(keys==null||keys.length<=0) {
            return;
        }
        boolean isDeleteKey = false;
        for(String filter:DELETEKEYFILTER) {
            if(StringUtils.startsWith(keys[0], filter)) {
                isDeleteKey = true;
                break;
            }
        }
        if(!isDeleteKey) {
            return;
        }
        if(StringUtils.isBlank(id)) {
            return;
        }
        List<String> list = context.get(id);
        if(list==null) {
            list = new ArrayList<String>();
            for(String key:keys) {
                list.add(key);
            }
            List<String> tempList = context.putIfAbsent(id, list);
            if(tempList != null) {
                for(String key:keys) {
                    tempList.add(key);
                }
            }
        }else {
            for(String key:keys) {
                if(!list.contains(key)) {
                    list.add(key);
                }
            }
        }
    }

    public static List<String> getDeleteKeyListById(String id){
        if(StringUtils.isBlank(id)) {
            return null;
        }
        List<String> list = context.get(id);
        boolean removeFlag = context.remove(id, list);
        if(!removeFlag) {
            return null;
        }
        return list;
    }
}
