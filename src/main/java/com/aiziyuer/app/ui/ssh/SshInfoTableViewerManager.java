package com.aiziyuer.app.ui.ssh;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

import com.aiziyuer.app.ssh.biz.SshInfoBizImpl;
import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ui.common.AbstractComposite;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoTableViewerManager {

	public Composite buildUI(Composite parent) {

		AbstractComposite area = null;

		XWT.setLoadingContext(new DefaultLoadingContext(this.getClass().getClassLoader()));

		// load XWT
		String name = SshInfoComposite.class.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX;
		try {

			URL url = SshInfoComposite.class.getResource(name);
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(IXWTLoader.CLASS_PROPERTY, this);
			options.put(IXWTLoader.CONTAINER_PROPERTY, parent);

			List<SessionInfoBO> sshInfoBOs = SshInfoBizImpl.getInstance().listSessionInfoBOs();
			SshInfoModle modle = new SshInfoModle();
			modle.getInput().addAll(sshInfoBOs);
			options.put(IXWTLoader.DATACONTEXT_PROPERTY, modle);

			area = (AbstractComposite) XWT.loadWithOptions(url, options);
			area.doLast();

		} catch (Throwable e) {
			log.error("Unable to load " + name, e);
		}

		return area;
	}

}
