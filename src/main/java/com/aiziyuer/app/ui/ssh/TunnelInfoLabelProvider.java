package com.aiziyuer.app.ui.ssh;

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
		return super.getColumnText(element, columnIndex);
	}
}