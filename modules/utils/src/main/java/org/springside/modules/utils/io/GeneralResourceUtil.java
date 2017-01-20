package org.springside.modules.utils.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;

/**
 * 兼容file://与classpath://的情况的工具集
 * 
 * @author calvin
 */
@Beta
public abstract class GeneralResourceUtil {

	public static final String CLASSPATH = "classpath://";

	public static final String FILE = "file://";

	/**
	 * 兼容file://与classpath://的情况的打开文件成Stream
	 */
	public static InputStream asStream(String generalPath) throws IOException {
		if (StringUtils.startsWith(generalPath, CLASSPATH)) {
			String resourceName = StringUtils.substringAfter(generalPath, CLASSPATH);
			return ResourceUtil.asStream(resourceName);
		} else if (StringUtils.startsWith(generalPath, FILE)) {
			String fileName = StringUtils.substringAfter(generalPath, FILE);
			return FileUtil.asInputStream(fileName);
		} else {
			throw new IllegalArgumentException("unsupport resoure type:" + generalPath);
		}
	}
}
