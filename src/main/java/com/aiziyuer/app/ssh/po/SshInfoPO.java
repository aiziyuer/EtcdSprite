package com.aiziyuer.app.ssh.po;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "SSH_INFO")
public class SshInfoPO {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** 目标主机 */
    @Column(name = "HOST")
    private String host;

    /** 连接用户名 */
    @Column(name = "NAME")
    private String userName;

    /** 连接用密码 */
    @Column(name = "PASSWORD")
    private String userPassword;

    /** 隧道入口 */
    @Column(name = "ENTRY_TUNNEL_PORT")
    private int entryTunnelPort;

    /** 隧道出口的主机 */
    @Column(name = "EXPORT_TUNNEL_HOST")
    private String exportTunnelHost;

    /** 隧道出口的端口 */
    @Column(name = "EXPORT_TUNNEL_PORT")
    private int exportTunnelPort;
}
