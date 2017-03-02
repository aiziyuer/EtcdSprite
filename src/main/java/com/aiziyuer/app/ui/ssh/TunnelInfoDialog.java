package com.aiziyuer.app.ui.ssh;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
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
	private Button okBtn;

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

		// XWT.getBindingContext(shell).bindValue(WidgetProperties.text(SWT.Modify).observe(sessionPortText),
		// BeanProperties.value("port").observe(modle),
		// new
		// UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).setConverter(new
		// StringToInteger()),
		// new
		// UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).setConverter(new
		// ObjectToString()));
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
