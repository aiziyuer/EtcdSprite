package com.aiziyuer.app;

import java.nio.file.Paths;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.aiziyuer.app.framework.util.ServiceLocator;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CoreApplication {

	static {
		new FileSystemXmlApplicationContext(
				Paths.get(System.getProperty("spring.config.path"),
						"applicationContext.xml").toString());
	}

	@Setter
	private String xwtFilePath;

	private void centerInDisplay(Shell shell) {
		Rectangle displayArea = shell.getDisplay().getClientArea();
		shell.setBounds(displayArea.width / 4, displayArea.height / 4,
				displayArea.width / 2, displayArea.height / 2);
	}

	private void run() {
		try {

			Shell shell = XWT.load(Paths.get(xwtFilePath).toUri().toURL())
					.getShell();

			shell.layout();
			centerInDisplay(shell);

			shell.open();
			while (!shell.isDisposed()) {
				if (!shell.getDisplay().readAndDispatch())
					shell.getDisplay().sleep();
			}

		} catch (Exception e) {
			log.error("System has error:", e);
		} finally {
			Display.getCurrent().dispose();
		}
	}

	public static void main(String[] args) {

		log.info("start gui start.");

		CoreApplication app = ServiceLocator.getInstance()
				.getBean("coreApplication");
		app.run();

		log.info("start gui end.");
	}
}
