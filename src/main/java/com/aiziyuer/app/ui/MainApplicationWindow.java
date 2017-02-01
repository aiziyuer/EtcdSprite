package com.aiziyuer.app.ui;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MainApplicationWindow {

	public void open() throws Exception {
		URL url = this.getClass()
				.getResource(MainApplicationWindow.class.getSimpleName()
						+ IConstants.XWT_EXTENSION_SUFFIX);
		Control control;
		control = XWT.load(url);
		Shell shell = control.getShell();
		shell.layout();
		centerInDisplay(shell);
		// run events loop
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		}
	}

	private void centerInDisplay(Shell shell) {
		Rectangle displayArea = shell.getDisplay().getClientArea();
		shell.setBounds(displayArea.width / 4, displayArea.height / 4,
				displayArea.width / 2, displayArea.height / 2);
	}

}
