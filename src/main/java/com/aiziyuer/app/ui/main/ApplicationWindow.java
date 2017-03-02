package com.aiziyuer.app.ui.main;

import java.util.List;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.ssh.biz.SshInfoBizImpl;
import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ui.common.AbstractWindow;
import com.aiziyuer.app.ui.common.CompositesFactory;
import com.aiziyuer.app.ui.ssh.SshInfoComposite;
import com.aiziyuer.app.ui.ssh.SshInfoModle;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ApplicationWindow extends AbstractWindow{

	@UI
	private CTabFolder cTabFolder;

	@UI
	private CTabItem cTabItem;

	public void onAboutMenuItemSelected(Event event) {
		log.info("onAboutMenuItemSelect");

		MessageDialog.openInformation(XWT.findShell(event.widget), "About",
				"Thanks you for using. \nCopy right by aiziyuer.");
	}

	public void onSshTunnelMenuItemSelected(Event event) {

		log.info("onSshTunnelMenuItemSelected");

		// 释放右侧的区域内容
		for (Control control : cTabFolder.getChildren())
			control.dispose();

		List<SessionInfoBO> sshInfoBOs = SshInfoBizImpl.getInstance().listSessionInfoBOs();
		SshInfoModle modle = new SshInfoModle();
		modle.getInput().addAll(sshInfoBOs);

		cTabItem.setControl(CompositesFactory.buildUI(cTabFolder, SshInfoComposite.class, modle));

		// 触发重新布局以显示新的内容
		cTabFolder.layout();
	}

}
