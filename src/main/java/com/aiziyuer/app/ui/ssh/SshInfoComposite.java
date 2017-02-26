package com.aiziyuer.app.ui.ssh;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.ui.common.AbstractComposite;

import lombok.extern.log4j.Log4j2;

@Log4j2
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

			log.info("tunnels list changed.");
			tunnelTableViewer.refresh();
		});

		sessionTableViewer.addDoubleClickListener((DoubleClickEvent event) -> {

			log.info("sessionTable double clicked.");

			EditSessionDialog dialog = new EditSessionDialog(this.getShell(), SWT.NONE);
			int result = dialog.open();
			if (result == SWT.OK) {
				log.info("press ok.");
			}

		});

	}

	@Override
	protected void addDataBinding() {

		IObservableValue targetObservableValue = ViewersObservables.observeSingleSelection(sessionTableViewer);
		IObservableValue modelObservableValue = BeanProperties.value("sessionInfoBO").observe(modle);
		XWT.getBindingContext(this).bindValue(targetObservableValue, modelObservableValue);

	}

	public void onMenuDetect(Event event) {

		log.info(event);

	}
}
