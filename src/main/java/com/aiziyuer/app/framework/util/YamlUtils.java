package com.aiziyuer.app.framework.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import lombok.extern.log4j.Log4j2;

/**
 * YAML文件解析类
 *
 */
@Log4j2
public class YamlUtils {

	/**
	 * 从yaml文件中加载为java总的bean类
	 * 
	 * @param filePath
	 *            yaml文件所在路径
	 * @param clazz
	 *            类
	 */
	public static <T> T load(String filePath, Class<T> clazz) {

		T obj = null;

		YamlReader reader = null;
		try {

			reader = new YamlReader(new FileReader(filePath));
			obj = reader.read(clazz);

		} catch (FileNotFoundException | YamlException e) {
			log.error(e);
		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(String.format("close yamlreader(filePath: %s) error", String.valueOf(filePath)), e);
				}
			}

		}

		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T load(String filePath)
	{
		T obj = null;

		YamlReader reader = null;
		try {

			reader = new YamlReader(new FileReader(filePath));
			obj = (T) reader.read();

		} catch (FileNotFoundException | YamlException e) {
			log.error(e);
		} finally {

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(String.format("close yamlreader(filePath: %s) error", String.valueOf(filePath)), e);
				}
			}

		}

		return obj;
	}

	/**
	 * 保存java bean对象到yaml文件
	 * 
	 * @param filePath
	 *            yaml文件路径
	 * @param bean
	 *            java bean对象
	 */
	public static void save(String filePath, Object bean) {

		YamlWriter writer = null;
		try {

			writer = new YamlWriter(new FileWriter(filePath));
			writer.write(bean);

		} catch (IOException e) {
			log.error(e);
		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (YamlException e) {
					log.error(String.format("close yamlwrite(filePath: %s) error", String.valueOf(filePath)), e);
				}
			}
		}
	}
}
