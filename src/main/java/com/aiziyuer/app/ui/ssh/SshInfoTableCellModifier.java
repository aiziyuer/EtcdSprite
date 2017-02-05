package com.aiziyuer.app.ui.ssh;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 *
 */
@Log4j2
public class SshInfoTableCellModifier implements ICellModifier {

	@Getter
	private TableViewer tv;

	public SshInfoTableCellModifier(TableViewer tv) {
		super();
		this.tv = tv;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {

		Object ret = null;

		try {
			ret = FieldUtils.readDeclaredField(element, property, true);
		} catch (IllegalAccessException e) {
			log.error(e);
		}

		return ret;
	}

	@Override
	public void modify(Object element, String property, Object value) {

		try {
			TableItem item = (TableItem) element;
			Object data = item.getData();
			FieldUtils.writeDeclaredField(data, property, value, true);

			int index = ArrayUtils.indexOf(tv.getColumnProperties(), property);
			if (index != ArrayUtils.INDEX_NOT_FOUND) {
				item.setText(index, String.valueOf(value));
			}
		} catch (IllegalAccessException e) {
			log.error(e);
		}

	}
}
