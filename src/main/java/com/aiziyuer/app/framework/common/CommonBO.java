package com.aiziyuer.app.framework.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 为子类添加属性监听与广播功能
 * @author aiziyuer
 *
 */
public abstract class CommonBO {

	/**
	 * 这里的名字是写死的, 如果这里的名字需要变更, 并且在子类中需要使用PropertyChangeSupport特性,
	 * 则需要在子类的属性<code>@Accessors</code>中加入
	 * <code>propertyChangeSupportFieldName = "propertySupport"</code>
	 */
	protected transient final PropertyChangeSupport propertySupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertySupport.firePropertyChange(propertyName, oldValue, newValue);
	}

}
