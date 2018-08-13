package com.multi.maven.dao.mysql.base.mapper;

import com.multi.maven.dao.mysql.base.mapper.provider.BatchLogicInsertMapperProvider;
import org.apache.ibatis.annotations.InsertProvider;

import java.util.List;

public interface BatchLogicInsertMapper<T> {

	@InsertProvider(type = BatchLogicInsertMapperProvider.class, method = "batchInsertList")
	int batchInsertList(List<T> arr);
}
