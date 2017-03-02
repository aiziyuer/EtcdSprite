package com.aiziyuer.app.ui.ssh;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ssh.bo.TunnelBO;
import com.aiziyuer.app.ui.common.AbstractComposite;
import com.aiziyuer.app.ui.common.WindowsFactory;

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

			SessionInfoBO sessionInfoBO = SerializationUtils.clone(modle.getSessionInfoBO());

			int result = WindowsFactory.open(getShell(), SessionInfoDialog.class, sessionInfoBO);
			if (result == SWT.OK) {
				// TODO 这里需要对比数据是否有修改
				try {
					BeanUtils.copyProperties(modle.getSessionInfoBO(), sessionInfoBO);
					sessionTableViewer.refresh();
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.error(e);
				}

				log.info("press ok.");
			}

		});

		tunnelTableViewer.addDoubleClickListener((DoubleClickEvent event) -> {

			log.info("sessionTable double clicked.");

			TunnelBO tunnelInfoBO = SerializationUtils.clone(modle.getTunnelBO());
			int result = WindowsFactory.open(getShell(), TunnelInfoDialog.class, tunnelInfoBO);
			if (result == SWT.OK) {

				// TODO 这里需要对比数据是否有修改
				try {
					BeanUtils.copyProperties(modle.getTunnelBO(), tunnelInfoBO);
					tunnelTableViewer.refresh();
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.error(e);
				}

				log.info("press ok.");
			}

		});

	}

	@Override
	protected void addDataBinding() {

		XWT.getBindingContext(this).bindValue(ViewersObservables.observeSingleSelection(sessionTableViewer),
				BeanProperties.value("sessionInfoBO").observe(modle));

		XWT.getBindingContext(this).bindValue(ViewersObservables.observeSingleSelection(tunnelTableViewer),
				BeanProperties.value("tunnelBO").observe(modle));

	}

	public void onMenuDetect(Event event) {

		log.info(event);

	}
}
