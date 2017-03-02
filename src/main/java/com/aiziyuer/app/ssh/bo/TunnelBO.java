package com.aiziyuer.app.ssh.bo;

import java.io.Serializable;

import com.aiziyuer.app.framework.common.CommonBO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 隧道信息
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TunnelBO extends CommonBO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 别名 */
	@Accessors(bound = true)
	private String alias;

	/** 本地隧道主机 */
	@Accessors(bound = true)
	private String localTunnelHost;

	/** 本地隧道端口 */
	@Accessors(bound = true)
	private String localTunnelPort;

	/**
	 * 是否是正向隧道 <br>
	 * true: 正向隧道,
	 * 相当于<code>ssh -NfL LOCAL_HOST:LOCAL_PORT:REMOTE_HOST:REMOTE_PORT lc@127.0.0.1 -p 2202</code><br>
	 * false: 反向隧道,
	 * 相当于<code>ssh -NfR 8080:127.0.0.1:80 lc@127.0.0.1 -p 2202</code>
	 */
	@Accessors(bound = true)
	private boolean local;

	/** 远端隧道主机 */
	@Accessors(bound = true)
	private String remoteTunnelHost;

	/** 远端隧道口 */
	@Accessors(bound = true)
	private String remoteTunnelPort;
}
