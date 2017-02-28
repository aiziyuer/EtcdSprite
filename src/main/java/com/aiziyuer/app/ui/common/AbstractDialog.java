package com.aiziyuer.app.ui.common;

import lombok.Getter;

/**
 * 对话框的抽象类, 每个对话框都会有一个结果
 *
 */
public class AbstractDialog {

	/** 对话框的返回值标示对话框的确认结果 */
	@Getter
	protected int result = 0;

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
