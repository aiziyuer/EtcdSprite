package com.aiziyuer.app.ui.ssh;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aiziyuer.app.ssh.bo.TunnelBO;
import com.aiziyuer.app.ui.common.AbstractWindow;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TunnelInfoDialog extends AbstractWindow {

	@UI
	private Shell shell;

	@UI
	private Text sessionPortText;

	@UI
	private Button okBtn, localBtn, remoteBtn;

	private TunnelBO modle;

	@Override
	protected void dataInit() {
		modle = (TunnelBO) XWT.getDataContext(shell);
		log.info("data init fininshed.", modle);
	}

	@Override
	protected void reLayout() {
		super.reLayout();

		// 对话框默认是确认键
		shell.setDefaultButton(okBtn);
	}

	@Override
	protected void addListener() {

	}

	@Override
	protected void addDataBinding() {

		// 绑定boolean字段的属性到radio的按钮组
		XWT.getBindingContext(shell).bindValue(new SelectObservableValue(Boolean.TYPE) {
			{
				addOption(Boolean.TRUE, SWTObservables.observeSelection(localBtn));
				addOption(Boolean.FALSE, SWTObservables.observeSelection(remoteBtn));
			}
		}, PojoObservables.observeValue(modle, "local"));

	}

	public void onOKButtonSelection(Event event) {
		result = SWT.OK;
		shell.dispose();
	}

	public void onCancelButtonSelection(Event event) {
		result = SWT.CANCEL;
		shell.dispose();
	}
}
