package com.aiziyuer.app.ssh.bo;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Log4j2
public class SshInfoBO extends Observable implements Observer, Serializable {

	/** 序列化的ID */
	private static final long serialVersionUID = 1L;

	/** 目标主机 */
	private String host;

	/** 连接用户名 */
	private String userName;

	/** 连接用密码 */
	private String userPassword;

	/** 隧道入口 */
	private String entryTunnelPort;

	/** 隧道出口的主机 */
	private String exportTunnelHost;

	/** 隧道出口的端口 */
	private String exportTunnelPort;

	@Override
	public void update(Observable newObj, Object oldObj) {

		log.info(String.format("newObj:%s", newObj));
		log.info(String.format("oldObj:%s", oldObj));
		
	}

}
