package com.aiziyuer.app.ssh.bo;

import java.io.Serializable;

import com.aiziyuer.app.framework.common.CommonBO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class SshInfoBO extends CommonBO implements Serializable {

	/** 序列化的ID */
	private static final long serialVersionUID = 1L;

	/** 目标主机 */
	@Setter
	@Accessors(bound = true, propertyChangeSupportFieldName = "propertySupport")
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
}
