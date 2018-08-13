package com.multi.maven.dao.redis.bean;


/**
 * Hash Bean interface
 * @author yangsongbo
 * @since v3.0
 *
 */
public interface IHashBean extends IRedisBean {

    /**
     * 获取Hash的Key
     * @return
     */
    public String getHashKey();

    public void setHashKey(String hashKey);

    /**获取Hash集成的Dto*/
//    public IDto dto();

    /**数据库或缓存中是否存在该记录*/
    public boolean isExist() ;

    /**数据库或缓存中是否存在该记录*/
    public void setExist(boolean isExist);
}
