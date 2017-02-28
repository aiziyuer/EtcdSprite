package com.aiziyuer.app.ui.ssh;

import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ui.common.AbstractDialog;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SessionInfoDialog extends AbstractDialog {

	@UI
	private Shell shell;

	@UI
	private Text sessionPortText;

	private SessionInfoBO modle;

	@Override
	protected void dataInit() {
		modle = (SessionInfoBO) XWT.getDataContext(shell);
		log.info("data init fininshed.", modle);
	}

	@Override
	protected void addListener() {

	}

	@Override
	protected void addDataBinding() {

		// IObservableValue targetObservableValue =
		// ViewersObservables.observeSingleSelection(sessionTableViewer);
		// IObservableValue modelObservableValue =
		// BeanProperties.value("sessionInfoBO").observe(modle);
		// XWT.getBindingContext(this).bindValue(targetObservableValue,
		// modelObservableValue);

		// IObservableValue portTarget =
		// WidgetProperties.text(SWT.Modify).observe(sessionPortText);
		// IObservableValue portModel =
		// BeanProperties.value("port").observe(modle);
		//
		// IConverter convertToStringArray =
		// IConverter.create(String.class, String[].class, (o1) -> ((String)
		// o1).split(","));
		// XWT.getBindingContext(this).bindValue(portTarget, portModel,
		// UpdateValueStrategy.create(convertToStringArray));
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
