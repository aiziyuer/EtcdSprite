package com.aiziyuer.app.ssh.po;

import lombok.Data;

@Data
public class SshInfoPO {

	/** 目标主机 */
	private String host;

	/** 连接用户名 */
	private String name;

	/** 连接用密码 */
	private String password;

	/** 隧道入口 */
	private int entryTunnelPort;

	/** 隧道出口的主机 */
	private String exportTunnelHost;

	/** 隧道出口的端口 */
	private int exportTunnelPort;
}
