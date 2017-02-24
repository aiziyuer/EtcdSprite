package com.aiziyuer.app.ssh.bo;

import java.util.List;

import com.aiziyuer.app.framework.common.CommonBO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class SessionInfoBO extends CommonBO  {

	/** 目标主机 */
	@Accessors(bound = true)
	private String host;

	/** 目标主机端口 */
	@Accessors(bound = true)
	private int port = 22;

	/** 连接用户名 */
	@Accessors(bound = true)
	private String userName;

	/** 连接用密码 */
	@Accessors(bound = true)
	private String userPassword;

	@Accessors(bound = true)
	public String status = SessionStatus.DISCONNECTED.toString();

	@Accessors(bound = true)
	private List<TunnelBO> tunnelBOs;

	public String getHostLabel() {
		return String.format("%s@%s:%d", userName, host, port);
	}
}
