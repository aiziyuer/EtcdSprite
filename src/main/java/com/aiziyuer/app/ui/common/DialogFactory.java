package com.aiziyuer.app.ui.common;

import java.net.URL;

import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import lombok.extern.log4j.Log4j2;

/**
 * 对话框工程, 用来生成对话框
 *
 */
@Log4j2
public class DialogFactory {

	/**
	 * 打开对话框
	 * 
	 * @return = SWT.CANCEL/SWT.OK
	 */
	public static <T> int open(Shell parent, Class<T> klass, Object dataContext) {
		Shell shell = createContents(parent, klass, dataContext);

		AbstractDialog dialog = (AbstractDialog) XWT.getCLR(shell);
		dialog.doLast();

		shell.open();
		shell.layout();
		Display display = parent.getDisplay();
		while (shell != null && !shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return dialog.getResult();
	}

	private static <T> Shell createContents(Shell parent, Class<T> klass, Object dataContext) {

		Shell shell = null;
		try {
			URL url = klass.getResource(klass.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
			Control control = XWT.load(parent, url, dataContext);
			shell = control.getShell();

			// 设置对话框居中显示
			Rectangle parentBounds = parent.getBounds();
			Rectangle childBounds = shell.getBounds();
			int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
			int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
			shell.setLocation(x, y);

		} catch (Exception e) {
			log.error(e);
		}

		return shell;
	}

}
