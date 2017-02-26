package com.aiziyuer.app.ui.ssh;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class EditSessionDialog extends Dialog {

	protected int result = SWT.CANCEL;;

	protected Shell shell;

	public EditSessionDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * 打开对话框
	 * 
	 * @return = SWT.CANCEL/SWT.OK
	 */
	public int open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {

		// 指定父shell, 如果父节点关闭, 自动释放子shell的资源
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);

		// 定制对话框的大小
		shell.setSize(287, 222);
		// logo
		shell.setImage(
				SWTResourceManager.getImage(EditSessionDialog.class, "/com/aiziyuer/app/ui/main/icons/logo.png"));
		// 标题
		shell.setText("SessionEditor");

		// 设置对话框居中显示
		Rectangle parentBounds = getParent().getBounds();
		Rectangle childBounds = shell.getBounds();
		int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
		int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
		shell.setLocation(x, y);
		shell.setLayout(new FillLayout(SWT.NONE));

		// 布局画布
		EditSessionComposite composite = new EditSessionComposite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

	}

}
