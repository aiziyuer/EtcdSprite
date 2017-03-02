package com.aiziyuer.app.ui.ssh;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;

import com.aiziyuer.app.ui.common.CommonTableLabelProvider;

public class TunnelInfoLabelProvider extends CommonTableLabelProvider {

	public TunnelInfoLabelProvider(TableViewer tv) {
		super(tv);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {

		String ret = super.getColumnText(element, columnIndex);
		String columeProperty = String.valueOf(tv.getColumnProperties()[columnIndex]);
		if (StringUtils.equalsIgnoreCase("local", columeProperty)) {
			ret = Boolean.valueOf(ret) ? "->" : "<-";
		}

		return ret;
	}
}