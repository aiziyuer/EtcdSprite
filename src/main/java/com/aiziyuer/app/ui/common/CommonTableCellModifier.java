package com.aiziyuer.app.ui.common;

import java.io.Serializable;
import java.util.Observable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
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
public class CommonTableCellModifier implements ICellModifier {

	@Getter
	private TableViewer tv;

	public CommonTableCellModifier(TableViewer tv) {
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

			// 如果数据没有发生改变则不作处理直接返回
			TableItem item = (TableItem) element;
			if (StringUtils.equalsIgnoreCase(item.getText(),
					String.valueOf(value))) {
				return;
			}

			Object data = item.getData();

			// 修改数据前做一次拷贝
			Object oldData = data instanceof Serializable
					? SerializationUtils.clone((Serializable) data) : null;

			// 通过反射修改原始的数据
			FieldUtils.writeDeclaredField(data, property, value, true);

			// 修改界面的数据
			int index = ArrayUtils.indexOf(tv.getColumnProperties(), property);
			if (index != ArrayUtils.INDEX_NOT_FOUND) {
				item.setText(index, String.valueOf(value));
			}

			tv.refresh();

			// 如果编辑的数据时支持被观察, 则数据修改后通知所有的观察者
			if (data instanceof Observable) {
				Observable observable = (Observable) data;
				FieldUtils.writeField(data, "changed", true, true);
				observable.notifyObservers(oldData);
			}
		} catch (IllegalAccessException e) {
			log.error(e);
		}

	}
}
