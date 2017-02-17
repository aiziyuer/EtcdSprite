package com.aiziyuer.app.ui.ssh;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

import com.aiziyuer.app.framework.util.ServiceLocator;
import com.aiziyuer.app.ssh.biz.ISshInfoBiz;
import com.aiziyuer.app.ssh.bo.SessionInfoBO;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoTableViewerManager {

	@Getter
	private WritableList input = new WritableList();

	public void buildUI(Composite parent) {

		XWT.setLoadingContext(
				new DefaultLoadingContext(this.getClass().getClassLoader()));

		// load XWT
		String name = SshInfoTableViewerComposite.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX;
		try {
			URL url = SshInfoTableViewerComposite.class.getResource(name);
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(IXWTLoader.CLASS_PROPERTY, this);
			options.put(IXWTLoader.CONTAINER_PROPERTY, parent);

			ISshInfoBiz sshInfoBiz = ServiceLocator.getInstance()
					.getBean("sshInfoBiz");
			List<SessionInfoBO> sshInfoBOs = sshInfoBiz.listSessionInfoBOs();
			input.addAll(sshInfoBOs);
			options.put(IXWTLoader.DATACONTEXT_PROPERTY, this);

			Composite area = (Composite) XWT.loadWithOptions(url, options);
			System.out.println(area);
		} catch (Throwable e) {
			log.error("Unable to load " + name, e);
		}
	}

}
