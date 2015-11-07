/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.mapper;

import java.util.List;

import com.google.common.collect.Lists;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * 简单封装orika, 实现深度转换Bean<->Bean的Mapper.
 */
public class BeanMapper {
	private static MapperFacade mapper = null;

	static {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapper = mapperFactory.getMapperFacade();

	}

	/**
	 * 基于Dozer转换对象的类型.
	 */
	public static <T> T map(Object source, Class<T> destinationClass) {
		return mapper.map(source, destinationClass);
	}

	/**
	 * 基于Dozer转换Collection中对象的类型.
	 */
	public static <T> List<T> mapList(Iterable sourceList, Class<T> destinationClass) {
		List<T> destinationList = Lists.newArrayList();
		for (Object sourceObject : sourceList) {
			T destinationObject = mapper.map(sourceObject, destinationClass);
			destinationList.add(destinationObject);
		}
		return destinationList;
	}

}