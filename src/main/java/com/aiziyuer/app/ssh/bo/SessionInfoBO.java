package com.aiziyuer.app.ssh.bo;

import java.io.Serializable;

import com.aiziyuer.app.framework.common.CommonBO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class SessionInfoBO extends CommonBO implements Serializable {

	/** 序列化的ID */
	private static final long serialVersionUID = 1L;

	/** 目标主机 */
	@Accessors(bound = true)
	private String host;

	/** 目标主机端口*/
	@Accessors(bound = true)
	private int port = 22;

	/** 连接用户名 */
	@Accessors(bound = true)
	private String userName;

	/** 连接用密码 */
	@Accessors(bound = true)
	private String userPassword;

}
