package com.aiziyuer.app.ui;

import java.util.List;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.framework.util.ServiceLocator;
import com.aiziyuer.app.ssh.biz.ISshInfoBiz;
import com.aiziyuer.app.ssh.bo.SshInfoBO;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainApplicationWindow {

	@UI
	private Composite rightContainer;

	private ISshInfoBiz sshInfoBiz = ServiceLocator.getInstance()
			.getBean("sshInfoBiz");
	@Getter
	private List<SshInfoBO> sshInfoBOs = sshInfoBiz.listSshInfoBOs();

	public void onAboutMenuItemSelected(Event event) {
		log.info("onAboutMenuItemSelect");

		MessageDialog.openInformation(XWT.findShell(event.widget), "About",
				"Thanks you for using. \nCopy right by aiziyuer.");
	}

	public void onSshTunnelMenuItemSelected(Event event) {
		// 这里需要更新右边的视图

		sshInfoBOs.addAll(sshInfoBiz.listSshInfoBOs());
	}

}
