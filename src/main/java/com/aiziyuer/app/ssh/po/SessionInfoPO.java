package com.aiziyuer.app.ssh.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "SESSION_INFO")
@ToString
public class SessionInfoPO {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** 目标主机 */
	@Column(name = "HOST")
	private String host;
	
	/** 目标主机端口*/
	@Column(name = "PORT")
	private int port;

	/** 连接用户名 */
	@Column(name = "NAME")
	private String userName;

	/** 连接用密码 */
	@Column(name = "PASSWORD")
	private String userPassword;

}
