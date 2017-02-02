package com.aiziyuer.app.framework.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import lombok.extern.log4j.Log4j2;

/**
 * 服务发现类
 */
@Log4j2
public class ServiceLocator implements BeanFactoryAware {

	private static BeanFactory beanFactory;

	private static ServiceLocator servlocator;

	private ServiceLocator() {
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		log.info("spring setBeanFactory");

		ServiceLocator.beanFactory = beanFactory;
	}

	public static synchronized ServiceLocator getInstance() {

		if (servlocator == null)
			servlocator = (ServiceLocator) beanFactory
					.getBean("serviceLocator");

		return servlocator;
	}

	/**
	 * 根据提供的bean名称得到相应的服务类
	 *
	 * @param beanName
	 *            bean名称
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanName) {
		return (T) beanFactory.getBean(beanName);
	}
}
