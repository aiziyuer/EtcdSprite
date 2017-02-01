package com.aiziyuer.app.ssh.bo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SshInfoBO {

	/** 目标主机 */
	private String host;

	/** 连接用户名 */
	private String name;

	/** 连接用密码 */
	private String password;

	/** 隧道入口 */
	private String entryTunnelPort;

	/** 隧道出口的主机 */
	private String exportTunnelHost;

	/** 隧道出口的端口 */
	private String exportTunnelPort;

}
