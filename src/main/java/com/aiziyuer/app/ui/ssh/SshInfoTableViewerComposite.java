package com.aiziyuer.app.ui.ssh;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.aiziyuer.app.framework.util.ServiceLocator;
import com.aiziyuer.app.ssh.biz.ISshInfoBiz;
import com.aiziyuer.app.ssh.bo.SshInfoBO;

import lombok.Getter;

public class SshInfoTableViewerComposite extends Composite {

	private ISshInfoBiz sshInfoBiz = ServiceLocator.getInstance()
			.getBean("sshInfoBiz");
	@Getter
	private List<SshInfoBO> sshInfoBOs = sshInfoBiz.listSshInfoBOs();

	public SshInfoTableViewerComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		// load XWT
		String name = SshInfoTableViewerComposite.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX;
		try {
			URL url = SshInfoTableViewerComposite.class.getResource(name);
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(IXWTLoader.CLASS_PROPERTY, this);
			options.put(IXWTLoader.CONTAINER_PROPERTY, this);
			options.put(IXWTLoader.DATACONTEXT_PROPERTY, this);
			XWT.setLoadingContext(new DefaultLoadingContext(
					this.getClass().getClassLoader()));
			XWT.loadWithOptions(url, options);
		} catch (Throwable e) {
			throw new Error("Unable to load " + name, e);
		}
	}

}
