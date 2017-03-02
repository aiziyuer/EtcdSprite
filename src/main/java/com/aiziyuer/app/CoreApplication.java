package com.aiziyuer.app;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;

import com.aiziyuer.app.ssh.biz.SshInfoBizImpl;
import com.aiziyuer.app.ui.common.WindowsFactory;
import com.aiziyuer.app.ui.main.ApplicationWindow;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CoreApplication {


	private void run() {
		try {
			
			WindowsFactory.open(null, ApplicationWindow.class, null);

		} catch (Exception e) {
			log.error("System has error:", e);
		} finally {
			Display.getCurrent().dispose();
		}
	}

	public static void main(String[] args) {

		log.info("start gui start.");
		
		SshInfoBizImpl.getInstance().listSessionInfoBOs();

		// 整个UI的操作放在UI的线程中执行, 与Main线程作出区分
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()), () -> {

			Thread.currentThread().setName("UIThread");
			CoreApplication app = new CoreApplication();
			app.run();

		});

		// 程序结束释放所有的session信息
		SshInfoBizImpl.getInstance().releaseSessions();

		log.info("start gui end.");
	}
}
