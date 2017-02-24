package com.aiziyuer.app.framework.util;

import java.nio.file.Paths;

/**
 * 文件路径静态类
 *
 */
public class PathConstant {

	/** 配置文件目录 */
	public final static String CONFIG_DIR;

	/** 数据文件所在目录 */
	public final static String DATA_DIR;

	static {
		CONFIG_DIR = Paths.get(System.getProperty("config.path")).toString();
		DATA_DIR = Paths.get(CONFIG_DIR, "data").toString();
	}

}
