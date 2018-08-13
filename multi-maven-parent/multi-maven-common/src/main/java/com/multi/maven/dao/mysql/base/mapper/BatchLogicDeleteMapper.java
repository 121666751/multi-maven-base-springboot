package com.multi.maven.dao.mysql.base.mapper;

import com.multi.maven.dao.mysql.base.mapper.provider.BatchLogicDeleteMapperProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

public interface BatchLogicDeleteMapper<T> {
	@UpdateProvider(type = BatchLogicDeleteMapperProvider.class, method = "dynamicSQL")
	int batchDelete(List<T> arr);
}
