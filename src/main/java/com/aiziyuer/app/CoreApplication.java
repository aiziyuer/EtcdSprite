package com.aiziyuer.app;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.aiziyuer.app.framework.util.ServiceLocator;
import com.aiziyuer.app.ssh.biz.ISshInfoBiz;
import com.aiziyuer.app.ui.main.MainApplicationWindow;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CoreApplication {

	static {
		new FileSystemXmlApplicationContext(
				Paths.get(System.getProperty("spring.config.path"), "applicationContext.xml").toString());
	}

	private void centerInDisplay(Shell shell) {
		Rectangle displayArea = shell.getDisplay().getPrimaryMonitor().getClientArea();
		shell.setBounds(displayArea.width / 4, displayArea.height / 4, displayArea.width / 2, displayArea.height / 2);
	}

	private void run() {
		try {

			Map<String, Object> options = new HashMap<String, Object>();
			options.put(IXWTLoader.CLASS_PROPERTY, this);
			options.put(IXWTLoader.CONTAINER_PROPERTY, null);

			Shell shell = XWT
					.loadWithOptions(
							MainApplicationWindow.class.getResource(
									MainApplicationWindow.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX),
							options)
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

		ISshInfoBiz sshInfoBiz = ServiceLocator.getInstance().getBean("sshInfoBiz");
		// List<TunnelBO> tunnelBOs = sshInfoBiz.listTunnelBos(1);
		// sshInfoBiz.createTunnels(tunnelBOs);

		// 整个UI的操作放在UI的线程中执行, 与Main线程作出区分
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()), () -> {

			Thread.currentThread().setName("UIThread");
			CoreApplication app = ServiceLocator.getInstance().getBean("coreApplication");
			app.run();

		});

		// 程序结束释放所有的session信息
		sshInfoBiz.releaseSessions();

		log.info("start gui end.");
	}
}
