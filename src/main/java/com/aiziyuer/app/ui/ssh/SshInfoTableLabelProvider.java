package com.aiziyuer.app.ui.ssh;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aiziyuer.app.ssh.bo.SshInfoBO;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoTableLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String ret = null;

		if (element instanceof SshInfoBO) {
			SshInfoBO sshInfoBO = (SshInfoBO) element;

			switch (columnIndex) {
			case 0:
				ret = sshInfoBO.getHost();
				break;
			case 1:
				ret = sshInfoBO.getUserName();
				break;
			default:
				break;
			}
		}

		if (ret == null)
			log.warn(String.format("cannot handle element:%s, columnIndex:%d.",
					String.valueOf(element), columnIndex));

		return ret;
	}

}
