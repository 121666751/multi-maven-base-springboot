package com.multi.maven.dao.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.multi.maven.dao.mongo.bean.IMongoBean;
import com.multi.maven.exception.BusinessException;
import com.multi.maven.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: litao
 * @Description:  所有 mongo的 实体必须实现 com.multi.maven.dao.mongo.bean.IMongoBean
 * @Date: 11:44 2018/8/8
 */
public class MongoClient {

    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String UPDATETIME = "updateTime";

    private static final String CREATETIME = "createTime";


    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }


    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public void insert(IMongoBean bean) {
        autoFill(bean);
        mongoTemplate.insert(bean);
    }


    public MongoConverter getConverter() {
        return mongoTemplate.getConverter();
    }


    /**
     * 执行一个聚合操作。
     *
     * @param aggregation 不得为空
     * @param inputType   不能为空
     * @param outputType  返回列表的参数化类型,不能为空。
     * @return 聚合操作的结果。
     */
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType) throws BusinessException {
        return mongoTemplate.aggregate(aggregation, inputType, outputType);
    }


    /**
     * <p>
     * 保存一批记录
     * </p>
     * <p>
     * 当mongoBean 中的id在数据库中已存在，抛出org.springframework.dao.DuplicateKeyException
     * </p>
     *
     * @param mongoBeans
     * @param entityClass
     */
    public <T extends IMongoBean> void insert(Collection<T> mongoBeans, Class<T> entityClass) throws BusinessException {
        Assert.notEmpty(mongoBeans,
                "this mongoBeans must not be empty: it must contain at least 1 element");
        Assert.notNull(entityClass, "the entityClass argument must be null");
        //出于性能考虑,批量插入不做自动填充操作,调用方决定填充项
//		autoFill(mongoBeans,entityClass);

        mongoTemplate.insert(mongoBeans, entityClass);
    }

    public <T extends IMongoBean> UpdateResult update(Query query, Update update, Class<T> entityClass) {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.updateMulti(query, update, entityClass);
    }

    public <T extends IMongoBean> UpdateResult updateFirst(Query query, Update update, Class<T> entityClass) {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.updateFirst(query, update, entityClass);
    }

    public <T extends IMongoBean> UpdateResult upsert(Query query, Update update, Class<T> entityClass) {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.upsert(query, update, entityClass);
    }

    /**
     * <p>
     * 返回指定查询的文档数量
     * </p>
     * <p>
     * Test Class:com.zhidoushi.mongo.TestMongo.count1()
     * </p>
     *
     * @param entityClass 集合Class
     * @return
     */
    public <T extends IMongoBean> long count(Class<T> entityClass) {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return count(null, entityClass);
    }


    /**
     * <p>返回指定查询的文档数量</p>
     *
     * @param query       查询对象
     * @param entityClass 集合Class
     * @return
     */
    public <T extends IMongoBean> long count(Query query, Class<T> entityClass) {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.count(query, entityClass);
    }


    /**
     * <p>保存或更新一条记录</p>
     * <p>如果id存在，更新，id在数据库中不存在，则保存</p>
     * <p>Test Class : com.zhidoushi.mongo.TestMongo.save()</p>
     *
     * @param mongoBean
     */
    public <T extends IMongoBean> void save(T mongoBean) throws BusinessException {
        Assert.notNull(mongoBean, "this mongoBean is required; it must not be null");
        autoFill(mongoBean);
        mongoTemplate.save(mongoBean);
    }


    /**
     * <p>
     * 查询记录并修改
     * </p>
     * <p>
     *
     * @param query       查询对象
     * @param update      更新对象
     * @param options     用来定义findAndModify的参数
     * @param entityClass 实体Class,不能为空
     */
    public <T extends IMongoBean> T findAndModify(Query query, Update update, FindAndModifyOptions options,
                                                  Class<T> entityClass) throws BusinessException {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");

        return mongoTemplate.findAndModify(query, update, options, entityClass);
    }


    /**
     * <p>查询一批记录</p>
     *
     * @param query       查询对象
     * @param entityClass 实体Class
     */
    public <T extends IMongoBean> List<T> find(Query query, Class<T> entityClass) throws BusinessException {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.find(query, entityClass);
    }


    /**
     * <p>查询全部记录</p>
     *
     * @param entityClass 实体Class
     */
    public <T extends IMongoBean> List<T> findAll(Class<T> entityClass) throws BusinessException {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");

        return mongoTemplate.findAll(entityClass);
    }


    /**
     * <p>查询一条记录</p>
     *
     * @param query       查询对象
     * @param entityClass 实体Class
     */
    public <T extends IMongoBean> T findOne(Query query, Class<T> entityClass) throws BusinessException {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.findOne(query, entityClass);
    }


    /**
     * <p>根据主键_id查询一条记录</p>
     *
     * @param _id         主键
     * @param entityClass 实体Class
     */
    public <T extends IMongoBean> T findById(String _id, Class<T> entityClass) throws BusinessException {
        Assert.notNull(entityClass, "the entityClass must be null");
        Assert.hasText(_id, "this _id must have text; it must not be null, empty, or blank");

        return mongoTemplate.findById(_id, entityClass);
    }


    /**
     * <p>
     * 根据query对象删除文档
     * </p>
     *
     * @param query       查询对象
     * @param entityClass
     */
    public <T extends IMongoBean> DeleteResult remove(Query query, Class<T> entityClass) throws BusinessException {
        Assert.notNull(entityClass, "this entityClass is required; it must not be null");
        return mongoTemplate.remove(query, entityClass);
    }


    /**
     * <p>
     * 根据mongoBean中的Id删除文档
     * </p>
     *
     * @param mongoBean 实体对象
     */
    public <T extends IMongoBean> DeleteResult remove(IMongoBean mongoBean) throws BusinessException {
        Assert.notNull(mongoBean, "this mongoBean is required; it must not be null");
        return mongoTemplate.remove(mongoBean);
    }


    private <T extends IMongoBean> T autoFill(T mongoBean) {
        if (mongoBean == null) {
            return mongoBean;
        }

        Map<String, Object> map = mongoBean.putFieldValueToMap();
        if (map != null && !map.isEmpty()) {
            Map<String, Object> temp = new HashMap<String, Object>();

            if (map.containsKey(UPDATETIME)) {
                temp.put(UPDATETIME, DateUtil.getCurUtilDate());
            }

            if (map.containsKey(CREATETIME) && map.get(CREATETIME) == null) {
                temp.put(CREATETIME, DateUtil.getCurUtilDate());
            }
            mongoBean.doMapToDtoValue(temp, false);
        }
        return mongoBean;
    }


    private <T extends IMongoBean> Collection<T> autoFill(Collection<T> mongoBeans, Class<T> entityClass) {
        if (mongoBeans == null || mongoBeans.isEmpty()) {
            return mongoBeans;
        }
        for (T t : mongoBeans) {
            Map<String, Object> map = t.putFieldValueToMap();
            if (map != null && !map.isEmpty()) {
                Map<String, Object> temp = new HashMap<String, Object>();

                if (map.containsKey(UPDATETIME)) {
                    temp.put(UPDATETIME, DateUtil.getCurUtilDate());
                }

                if (map.containsKey(CREATETIME) && map.get(CREATETIME) == null) {
                    temp.put(CREATETIME, DateUtil.getCurUtilDate());
                }

                t.doMapToDtoValue(temp, false);
            }
        }
        return mongoBeans;
    }

}
