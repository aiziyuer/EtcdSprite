package com.aiziyuer.app.ssh.bo;

import org.apache.commons.lang3.StringUtils;

public enum SessionStatus {

	CONNECTED("connected"), DISCONNECTED("disconnected");

	private String desc;

	private SessionStatus(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return desc;
	}

	/**
	 * 根据字符串解析出状态
	 * 
	 * @param statusStr
	 *            字符串形式的状态
	 */
	public static SessionStatus parse(String statusStr) {
		SessionStatus ret = DISCONNECTED;
		for (SessionStatus s : SessionStatus.values()) {
			if (StringUtils.equalsIgnoreCase(s.desc, statusStr)) {
				ret = s;
				break;
			}
		}
		return ret;
	}
}
