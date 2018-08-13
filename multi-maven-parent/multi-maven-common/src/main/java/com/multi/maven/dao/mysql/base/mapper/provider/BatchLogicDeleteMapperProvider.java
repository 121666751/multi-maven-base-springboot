package com.multi.maven.dao.mysql.base.mapper.provider;

import com.multi.maven.constant.DBConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SetSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;
import org.apache.ibatis.scripting.xmltags.WhereSqlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public class BatchLogicDeleteMapperProvider extends MapperTemplate {
	

	public BatchLogicDeleteMapperProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
		super(mapperClass, mapperHelper);
	}

	public SqlNode batchDelete(MappedStatement ms) {
		// 获取实体类型
		Class<?> entityClass = getEntityClass(ms);
		log.debug("要更新的实体类："+entityClass.getName());
		List<SqlNode> sqlNodes = new LinkedList<>();
		// update table
		sqlNodes.add(new StaticTextSqlNode("UPDATE " + tableName(entityClass)));
		// 获取全部列
		Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
		EntityColumn drCol = null;
		EntityColumn idCol = null;
		for (EntityColumn entityColumn : columnList) {
			if (DBConsts.FIELD_DR.equalsIgnoreCase(entityColumn.getColumn())) {
				drCol = entityColumn;
			}
			if (entityColumn.isId()) {
				idCol = entityColumn;
			}
		}

		sqlNodes.add(new SetSqlNode(ms.getConfiguration(), new StaticTextSqlNode(drCol.getColumn() + " = " + DBConsts.DR_DELETE)));

		ForEachSqlNode forEachSqlNode = new ForEachSqlNode(ms.getConfiguration(),
				new StaticTextSqlNode("#{item." + idCol.getProperty() + "}"), "list", "index", "item", " id in (", ")",
				",");

		sqlNodes.add(new WhereSqlNode(ms.getConfiguration(), forEachSqlNode));

		return new MixedSqlNode(sqlNodes);
	}
}
