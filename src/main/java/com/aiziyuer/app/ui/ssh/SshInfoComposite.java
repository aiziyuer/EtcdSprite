package com.aiziyuer.app.ui.ssh;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.ui.common.AbstractComposite;

public class SshInfoComposite extends AbstractComposite {

	@UI
	private TableViewer sessionTableViewer;

	@UI
	private TableViewer tunnelTableViewer;

	private SshInfoModle modle;

	public SshInfoComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
	}

	@Override
	protected void dataInit() {
		modle = (SshInfoModle) XWT.getDataContext(this);
	}

	@Override
	protected void addListener() {

		modle.getTunnels().addListChangeListener((ListChangeEvent event) -> {
			tunnelTableViewer.refresh();
		});

	}

	@Override
	protected void addDataBinding() {

		IObservableValue targetObservableValue = ViewersObservables.observeSingleSelection(sessionTableViewer);
		IObservableValue modelObservableValue = BeanProperties.value(modle.getClass(), "sessionInfoBO").observe(modle);
		XWT.getBindingContext(this).bindValue(targetObservableValue, modelObservableValue);

	}

	public void onMenuDetect(Event event) {
		System.out.println(event);
	}
}
