package com.aiziyuer.app.ui.ssh;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.aiziyuer.app.ssh.bo.SessionStatus;
import com.aiziyuer.app.ui.common.CommonTableLabelProvider;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoTableLabelProvider extends CommonTableLabelProvider {

	private static final Map<String, String> SESSION_STATUS_IMAGE_PATH_MAP = new HashMap<String, String>();

	{
		SESSION_STATUS_IMAGE_PATH_MAP.put(SessionStatus.CONNECTED.toString(), "icons/bullet_green.png");
		SESSION_STATUS_IMAGE_PATH_MAP.put(SessionStatus.DISCONNECTED.toString(), "icons/bullet_red.png");
	}

	public SshInfoTableLabelProvider(TableViewer tv) {
		super(tv);
	}

	public Image getColumnImage(Object element, int columnIndex) {

		if (columnIndex == 0) {
			try {
				String sessionStatus = String.valueOf(
						PropertyUtils.getProperty(element, String.valueOf(tv.getColumnProperties()[columnIndex])));

				return SWTResourceManager.getImage(this.getClass(), SESSION_STATUS_IMAGE_PATH_MAP.get(sessionStatus));

			} catch (Exception e) {
				log.error(e);
			}
		}

		return null;
	}

	public String getColumnText(Object element, int columnIndex) {

		if (columnIndex == 0) {
			return null;
		}

		return super.getColumnText(element, columnIndex);
	}
}