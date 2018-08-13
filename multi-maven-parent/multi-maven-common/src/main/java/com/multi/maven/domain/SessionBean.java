package com.multi.maven.domain;


import com.multi.maven.dao.mysql.bean.IBaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Seesion Bean
 * 
 * @author yangsongbo
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SessionBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private IBaseModel userDto;

	private List<String> rightIds;

}
