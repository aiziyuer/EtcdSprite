package com.aiziyuer.app.ui.main;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.ui.ssh.SshInfoTableViewerComposite;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainApplicationWindow {

	@UI
	private Composite rightContainer;

	public void onAboutMenuItemSelected(Event event) {
		log.info("onAboutMenuItemSelect");

		MessageDialog.openInformation(XWT.findShell(event.widget), "About",
				"Thanks you for using. \nCopy right by aiziyuer.");
	}

	public void onSshTunnelMenuItemSelected(Event event) {
		
		log.info("onSshTunnelMenuItemSelected");

		// 释放右侧的区域内容
		for (Control control : rightContainer.getChildren())
			control.dispose();

		new SshInfoTableViewerComposite(rightContainer, SWT.NONE);
		
		// 触发重新布局以显示新的内容
		rightContainer.layout();
	}

}
