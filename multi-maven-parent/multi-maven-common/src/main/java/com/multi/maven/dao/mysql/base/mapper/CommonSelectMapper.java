package com.multi.maven.dao.mysql.base.mapper;

import com.github.pagehelper.PageInfo;
import com.multi.maven.dao.mysql.base.mapper.provider.CommonSelectMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;


public interface CommonSelectMapper<T> {

	@SelectProvider(type = CommonSelectMapperProvider.class, method = "dynamicSQL")
	PageInfo<T> selectLikePage(int pageIndex, int pageSize, @Param("searchValue") String searchValue);

	@SelectProvider(type = CommonSelectMapperProvider.class, method = "dynamicSQL")
	PageInfo<T> selectPage(int pageIndex, int pageSize);

	@SelectProvider(type = CommonSelectMapperProvider.class, method = "dynamicSQL")
	List<T> selectByIdList(List<String> idList);

}
