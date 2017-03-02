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
public class WindowsFactory {

	/**
	 * 打开对话框
	 * 
	 * @return = SWT.CANCEL/SWT.OK
	 */
	public static <T extends AbstractWindow> int open(Shell parent, Class<T> klass, Object dataContext) {
		Shell shell = createContents(parent, klass, dataContext);

		AbstractWindow w = (AbstractWindow) XWT.getCLR(shell);
		w.doLast();

		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (shell != null && !shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return w.getResult();
	}

	/**
	 * 创建窗体的内容
	 * 
	 * @param parent
	 *            窗体的父类
	 * @param klass
	 *            窗体对应的java类
	 * @param dataContext
	 *            上下文
	 */
	private static <T> Shell createContents(Shell parent, Class<T> klass, Object dataContext) {

		Shell shell = null;
		try {
			URL url = klass.getResource(klass.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
			Control control = XWT.load(parent, url, dataContext);
			shell = control.getShell();

			if (parent != null) {

				// 如果有父类, 则设置窗口居中显示
				Rectangle parentBounds = parent.getBounds();
				Rectangle childBounds = shell.getBounds();
				int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
				int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
				shell.setLocation(x, y);
			} else {

				// 如果没有父类, 则设置窗口为主显示屏幕的中间显示
				Rectangle displayArea = shell.getDisplay().getPrimaryMonitor().getClientArea();
				shell.setBounds(displayArea.width / 4, displayArea.height / 4, displayArea.width / 2,
						displayArea.height / 2);
			}

		} catch (Exception e) {
			log.error(e);
		}

		return shell;
	}

}
