package com.multi.maven.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.multi.maven.constant.DBConsts;
import com.multi.maven.dao.mysql.base.mapper.BaseMapper;
import com.multi.maven.dao.mysql.bean.IBaseModel;
import com.multi.maven.exception.BusinessException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: litao
 * @Description: mysql 基础service
 * @Date: 11:25 2018/8/9
 */
public abstract class BaseService<T extends IBaseModel> extends BaseAbstractService{

    @Autowired
    public IDGeneratorService idGeneratorService;

    public List<T> selectAll() throws Exception {
        T t = getGenericType().newInstance();
        t.setEnabled(DBConsts.DR_NORMAL);
        return getBaseMapper().select(t);
    }

    public PageInfo<T> selectPage(int pageNum, int pageSize) {
        PageInfo<T> pageInfo = new PageInfo<>();
        Page<T> page = PageHelper.startPage(pageNum, pageSize);
        List<T> list = getBaseMapper().selectAll();
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 获取泛型类型
     *
     * @return 泛型类型
     */
    private Class<T> getGenericType() throws BusinessException {
        // 通过dao找到泛型类的类型
        Class<T> cls = getGenericType(getBaseMapper().getClass());
        if (cls == null) {
            throw new BusinessException("无法获取Mapper<T>泛型类型:" + getBaseMapper().getClass().getName());
        }

        return cls;
    }

    private Class<T> getGenericType(Class cls) {
        Type[] types = cls.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                return (Class<T>) t.getActualTypeArguments()[0];
            } else if (type instanceof Class) {
                return getGenericType((Class<T>) type);
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public int save(T entity) throws BusinessException {
        int result;
        try {
            //entity.setId(IdGenerator.getInstance().nextId(getModelCode()));
            entity.setEnabled(DBConsts.DR_NORMAL);
            setCreateTs(entity);
            result = getBaseMapper().insert(entity);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 插入数据,忽略null
     *
     * @param entity
     * @return
     * @throws BusinessException
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveSelective(T entity) throws BusinessException {
        int result;
        try {
            entity.setEnabled(DBConsts.DR_NORMAL);
            setCreateTs(entity);
            result = getBaseMapper().insertSelective(entity);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public int save(List<T> arr) throws BusinessException {

        Date ts = createTs();

        for (T t : arr) {
            // t.setId(IdGenerator.getInstance().nextId(getModelCode()));
//            t.setEnabled(DBConsts.DR_NORMAL);
            t.setUpdateTime(ts);
        }

        int result;

        try {
            result = getBaseMapper().insertList(arr);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }

        return result;
    }

    public Date createTs() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ts;
        try {
            ts = format.parse(format.format(new Date()));
        } catch (ParseException e) {
            ts = new Date();
        }

        return ts;
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete(T entity) throws BusinessException {
        int result;
        T newEntity;

        try {
            validateTs(entity);
            newEntity = (T) entity.getClass().newInstance();
            newEntity.setId(entity.getId());
            setUpdateTs(newEntity);

            result = getBaseMapper().updateByPrimaryKeySelective(newEntity);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 根据条件删除
     *
     * @param entity
     * @return
     * @throws BusinessException
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByCondition(T entity, Example example) throws BusinessException {
        int result = 0;
        T newEntity;
        try {
            validateTs(entity);
            newEntity = (T) entity.getClass().newInstance();
            newEntity.setId(entity.getId());
            newEntity.setEnabled(DBConsts.DR_DELETE);
            setUpdateTs(newEntity);

            result = updateByConditionSelective(newEntity, example);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public int update(T entity) throws BusinessException {
        int result;

        try {
            validateTs(entity);
            setUpdateTs(entity);
            result = getBaseMapper().updateByPrimaryKey(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateSelective(T entity) throws BusinessException {
        int result;

        try {
            validateTs(entity);
            setUpdateTs(entity);
            result = getBaseMapper().updateByPrimaryKeySelective(entity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 条件更新,不忽略字段为空的值
     *
     * @param entity
     * @param example
     * @return
     * @throws BusinessException
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateByCondition(T entity, Example example) throws BusinessException {
        int result = 0;
        try {
            validateTs(entity);
            setUpdateTs(entity);
            result = getBaseMapper().updateByExample(entity, example);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(List<T> entitys) throws BusinessException {
        int result = 0;
        for (T entity : entitys) {
            result += update(entity);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateSelective(List<T> entitys) throws BusinessException {
        int result = 0;
        for (T entity : entitys) {
            result += updateSelective(entity);
        }
        return result;
    }

    /**
     * 条件更新,忽略字段为空的值
     *
     * @param entity
     * @param example
     * @return
     * @throws BusinessException
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateByConditionSelective(T entity, Example example) throws BusinessException {
        int result = 0;
        try {
            validateTs(entity);
            setUpdateTs(entity);
            result = getBaseMapper().updateByExampleSelective(entity, example);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }
        return result;
    }

    private void validateTs(T entity) throws BusinessException {
        if (entity == null || entity.getUpdateTime() == null) {
            return;
        }

//        T origEntity = getBaseMapper().selectByPrimaryKey(entity.getId());

//        if (entity.getUpdateTime().compareTo(origEntity.getUpdateTime()) != 0) {
//            throw new BusinessException("当前数据已被他人修改，请刷新！");
//        }
    }

    /**
     * 设置 updateTime
     *
     * @param entity
     * @throws BusinessException
     */
    protected void setUpdateTs(T entity) throws BusinessException {
        // 由于mysql不支持毫秒级,所以设置TS到秒
        Date currentDate = createTs();
        entity.setUpdateTime(currentDate);
    }

    /**
     * 设置 updateTime 和 createTime
     *
     * @param entity
     * @throws BusinessException
     */
    protected void setCreateTs(T entity) throws BusinessException {
        // 由于mysql不支持毫秒级,所以设置TS到秒
        entity.setEnabled(DBConsts.DR_NORMAL);
        Date currentDate = createTs();
        entity.setCreateTime(currentDate);
        entity.setUpdateTime(currentDate);
    }

    @SuppressWarnings("unchecked")
    public int updateStatus(T entity, short status) throws BusinessException {

        int result;

        T newEntity;

        try {
            validateTs(entity);
            newEntity = (T) entity.getClass().newInstance();
            newEntity.setId(entity.getId());
            PropertyUtils.setSimpleProperty(newEntity, DBConsts.FIELD_STATUS, status);
            setUpdateTs(newEntity);
            result = getBaseMapper().updateByPrimaryKeySelective(newEntity);

        } catch (Exception e) {
            throw new BusinessException(e.getMessage(), e);
        }

        return result;
    }

    public int enable(T entity) throws BusinessException {
        return updateStatus(entity, DBConsts.STATUS_ENABLE);
    }

    public int enable(List<T> entitys) throws BusinessException {
        int result = 0;
        for (T entity : entitys) {
            result += enable(entity);
        }
        return result;
    }

    public int disable(T entity) throws BusinessException {
        return updateStatus(entity, DBConsts.STATUS_DISABLE);
    }

    public int disable(List<T> entitys) throws BusinessException {
        int result = 0;
        for (T entity : entitys) {
            result += disable(entity);
        }
        return result;
    }

    public T selectByPrimaryKey(Object key) throws BusinessException {
        return getBaseMapper().selectByPrimaryKey(key);
    }

    public List<T> selectByCondition(Condition condition) throws BusinessException {

        return getBaseMapper().selectByCondition(condition);
    }

    public PageInfo<T> selectPageByCondition(Condition condition, int pageNum, int pageSize) throws BusinessException {
        PageInfo<T> pageInfo = new PageInfo<>();
        Page<T> page = PageHelper.startPage(pageNum, pageSize);
        List<T> list = selectByCondition(condition);
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }


    public List<T> selectByExample(T entity) throws BusinessException {
        Class clz = entity.getClass();
        Field[] fields = clz.getDeclaredFields();
        //Get All fields of Supper Class
        if (clz.getSuperclass() != null) {
            Class clzSup = clz.getSuperclass();
            fields = (Field[]) ArrayUtils.addAll(fields, clzSup.getDeclaredFields());
        }

        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        for (Field field : fields) {
            String val;
            try {
                val = BeanUtils.getProperty(entity, field.getName());
            } catch (IllegalAccessException e) {
                throw new BusinessException(e);
            } catch (InvocationTargetException e) {
                throw new BusinessException(e);
            } catch (NoSuchMethodException e) {
                throw new BusinessException(e);
            }

            if (!StringUtils.isEmpty(val)) {
                criteria.andEqualTo(field.getName(), val);
            }
        }

        return getBaseMapper().selectByExample(example);
    }

    public List<T> selectByEntity(T entity) throws BusinessException {
        return getBaseMapper().select(entity);
    }

    public PageInfo<T> selectPageByExample(T entity, int pageNum, int pageSize) throws BusinessException {
        PageInfo<T> pageInfo = new PageInfo<>();
        Page<T> page = PageHelper.startPage(pageNum, pageSize);
        List<T> list = selectByExample(entity);
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    /**
     * 根据条件查询一条
     *
     * @param entity
     * @return @see {@link BusinessException}
     * @throws BusinessException
     */
    public T selectOneByEntity(T entity) throws BusinessException {
        return getBaseMapper().selectOne(entity);
    }

    @Autowired
    public BaseMapper<T> baseMapper;

    public BaseMapper<T> getBaseMapper() {
        return this.baseMapper;
    }
    /**
     * 封装Criteria参数
     * 注意：
     * （1）不支持date类型
     * （2）定义类型要和数据库的字段名进行匹配；
     * @param object  入参对象
     * @param t  表的对应的bean的class
     * @author wangrui
     * @return
     * @throws Exception
     */
    private Condition  packageCriteriaByParam(Object object, T t) throws Exception{
        Condition condition = new Condition(t.getClass());
        Criteria criteria = condition.createCriteria().andEqualTo("enabled",DBConsts.DR_NORMAL);//默认可用
        //获取实体对象所对应的域
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field=declaredFields[i];//属性
            field.setAccessible(true);//可访问
            String fieldName = field.getName();//属性名
            Class<?> fieldType = field.getType();//属性的类型
            Object val = field.get(object);//属性的值
            if(!"class java.util.Date".equals(fieldType.toString())&&val!=null&&!DBConsts.PAGE_NUM.equals(fieldName)&&!DBConsts.PAGE_SIZE.equals(fieldName)){
                criteria.andEqualTo(fieldName,val);
            }
        }
        return condition;
    }
}
