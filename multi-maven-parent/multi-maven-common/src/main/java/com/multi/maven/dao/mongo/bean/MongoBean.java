package com.multi.maven.dao.mongo.bean;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

/**
 * @author: litao
 * @see:
 * @description: 所有的MongoBean继承该该接口
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Getter
@Setter
public abstract class MongoBean implements IMongoBean{

    private ObjectId _id;

    @Override
    public String getPk() {
        return _id != null ? _id.toHexString() : null;
    }

    @Override
    public void setPk(String pk) {
        this._id = new ObjectId(pk);
    }

}
