/*******************************************************************************
 * Copyright (c) 2005, 2017 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.springside.modules.metrics;

/**
 * 不同于定时计算并汇报的Reporter, Exporter以JMX/RESTFul等方式，暴露所有提供Metrics的实时查询接口.
 * 
 * 但Exporter不会触发Metric计算，只会读取由ReporterScheduler产生的快照值
 */
public interface Exporter {
}
