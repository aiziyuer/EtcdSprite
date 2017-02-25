package com.aiziyuer.app.ui.common;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractComposite extends Composite {

	public AbstractComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * 在完成xwt界面布局后再做一次布局调整
	 */
	protected void reLayout() {
	}

	/**
	 * 添加监听器
	 */
	protected void addListener() {
	}

	/**
	 * 添加数据绑定
	 */
	protected void addDataBinding() {
	}

	/**
	 * 数据初始化
	 */
	protected void dataInit() {

	}

	public final void doLast() {

		dataInit();

		reLayout();

		addListener();

		addDataBinding();
	}

}
