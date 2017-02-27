package com.aiziyuer.app.ui.ssh;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class SessionInfoDialog {

	private Shell shell;

	private int result = 0;

	/**
	 * 打开对话框
	 * 
	 * @return = SWT.CANCEL/SWT.OK
	 */
	public int open(Shell parent, Object dataContext) {
		shell = createContents(parent, dataContext);
		shell.open();
		shell.layout();
		Display display = parent.getDisplay();
		while (shell != null && !shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private Shell createContents(Shell parent, Object dataContext) {

		try {
			URL url = SessionInfoDialog.class
					.getResource(SessionInfoDialog.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
			
			Control control = XWT.load(parent, url, dataContext);
			shell = control.getShell();

			// 设置对话框居中显示
			Rectangle parentBounds = parent.getBounds();
			Rectangle childBounds = shell.getBounds();
			int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
			int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
			shell.setLocation(x, y);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return shell;
	}

	public void onSelection(Event event) {
		System.out.println("hello");
	}
}
