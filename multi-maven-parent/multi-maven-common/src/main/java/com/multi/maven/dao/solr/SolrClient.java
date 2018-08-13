package com.multi.maven.dao.solr;

import com.multi.maven.dao.solr.bean.ISolrBean;
import com.multi.maven.exception.BaseException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class SolrClient {

	private String zkHost;

	@Autowired
	private CloudSolrClient client;
	
	public String getZkHost() {
		return zkHost;
	}

	public void setZkHost(String zkHost) {
		this.zkHost = zkHost;
	}

	/*public void init() {
		 client = new CloudSolrClient(zkHost);
		 //
	}*/
	
	public void destroy() throws IOException {
		client.close();
	}
	
	public void save(ISolrBean bean) {
		try {
			client.setDefaultCollection(bean.getClass().getSimpleName());
			client.addBean(bean.getClass().getSimpleName(), bean);
			client.commit();
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}
	
	public void deleteByPK(ISolrBean bean) {
		try {
			client.setDefaultCollection(bean.getClass().getSimpleName());
			client.deleteById(bean.getClass().getSimpleName(), bean.getPK());
			client.commit();
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}
	
	
	public <T extends ISolrBean> void deleteAll(Class<T> entityClass) {
		try {
			client.setDefaultCollection(entityClass.getSimpleName());
			client.deleteByQuery(entityClass.getSimpleName(),"*:*");
			client.commit();
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}
	
	
	public <T extends ISolrBean> List<T> searchForList(SolrQuery query,Class<T> beanClass){
		try {
			client.setDefaultCollection(beanClass.getSimpleName());
			QueryResponse response = client.query(beanClass.getSimpleName(), query);
			return response.getBeans(beanClass);
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}
	
	public <T extends ISolrBean> QueryResponse search(SolrQuery query,Class<T> beanClass){
		try {
			client.setDefaultCollection(beanClass.getSimpleName());
			QueryResponse response = client.query(beanClass.getSimpleName(), query);
			return response;
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}
	
}
