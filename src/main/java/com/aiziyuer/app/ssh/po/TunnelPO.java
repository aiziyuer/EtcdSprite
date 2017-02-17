package com.aiziyuer.app.ssh.po;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "TUNNEL_INFO")
@ToString
@EqualsAndHashCode(callSuper = false)
public class TunnelPO {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** 别名 */
	@Column(name = "ALIAS")
	private String alias;

	/** 本地隧道主机 */
	@Column(name = "LOCAL_TUNNEL_HOST")
	private String localTunnelHost;

	/** 本地隧道端口 */
	@Column(name = "LOCAL_TUNNEL_PORT")
	private String localTunnelPort;

	/**
	 * 是否是正向隧道 <br>
	 * true: 正向隧道,
	 * 相当于<code>ssh -NfL LOCAL_HOST:LOCAL_PORT:REMOTE_HOST:REMOTE_PORT lc@127.0.0.1 -p 2202</code><br>
	 * false: 反向隧道,
	 * 相当于<code>ssh -NfR 8080:127.0.0.1:80 lc@127.0.0.1 -p 2202</code>
	 */
	@Column(name = "IS_LOCAL")
	private boolean isLocal;

	/** 远端隧道主机 */
	@Column(name = "REMOTE_TUNNEL_HOST")
	private String remoteTunnelHost;

	/** 远端隧道口 */
	@Column(name = "REMOTE_TUNNEL_PORT")
	private String remoteTunnelPort;

	/** SessionInfo信息 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "SESSION_INFO_ID", unique = true)
	private SessionInfoPO sessionInfoPO;
}
