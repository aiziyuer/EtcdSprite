package com.aiziyuer.app.ui;

import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.framework.util.ServiceLocator;
import com.aiziyuer.app.ssh.biz.ISshInfoBiz;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainApplicationWindow {

	private ISshInfoBiz sshInfoBiz = ServiceLocator.getInstance()
			.getBean("sshInfoBiz");

	public void clickBtn(Event event) {

		log.info(sshInfoBiz.listSshInfoBOs());

		log.info("hello");
	}

}
