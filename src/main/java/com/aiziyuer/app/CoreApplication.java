package com.aiziyuer.app;

import com.aiziyuer.app.framework.util.ServiceLocator;
import com.aiziyuer.app.ssh.biz.ISshInfoBiz;
import lombok.extern.log4j.Log4j2;
import org.eclipse.swt.widgets.Display;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.nio.file.Paths;

@Log4j2
public class CoreApplication {

	static {
		new FileSystemXmlApplicationContext(
				Paths.get(System.getProperty("config.path"),
						"applicationContext.xml").toString());
	}

	public static void main(String[] args) {

		log.info("start gui start.");

		try {

			ISshInfoBiz sshInfoBiz = ServiceLocator.getInstance()
					.getService("sshInfoBiz");
			log.info(sshInfoBiz.listSshInfoBOs());

			MainApplicationWindow window = new MainApplicationWindow();

			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();

		} catch (Exception e) {
			log.error("System has error:", e);
		}

		log.info("start gui end.");
	}
}
