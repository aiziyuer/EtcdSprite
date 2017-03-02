package com.aiziyuer.app.ui.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.swt.widgets.Composite;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CompositesFactory {

	public static <T extends AbstractComposite> Composite buildUI(Composite parent, Class<T> klass,
			Object dataContext) {

		AbstractComposite area = null;

		XWT.setLoadingContext(new DefaultLoadingContext(klass.getClassLoader()));

		// load XWT
		String name = klass.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX;
		try {

			URL url = klass.getResource(name);
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(IXWTLoader.CONTAINER_PROPERTY, parent);
			options.put(IXWTLoader.DATACONTEXT_PROPERTY, dataContext);

			area = (AbstractComposite) XWT.loadWithOptions(url, options);
			area.doLast();

		} catch (Throwable e) {
			log.error("Unable to load " + name, e);
		}

		return area;
	}

}
