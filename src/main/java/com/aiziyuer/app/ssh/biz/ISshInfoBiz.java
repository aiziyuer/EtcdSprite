package com.aiziyuer.app.ssh.biz;

import java.util.List;

import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ssh.bo.TunnelBO;

public interface ISshInfoBiz {

	/**
	 * 查询所有的Session信息
	 * 
	 * @return 返回所有的连接信息
	 */
	List<SessionInfoBO> listSessionInfoBOs();

	/**
	 * 查询隧道信息
	 * 
	 * @param sessionInfoId
	 *            session的标示
	 * 
	 * @return session对应的隧道信息
	 */
	List<TunnelBO> listTunnelBos(long sessionInfoId);

	/**
	 * 查询隧道信息
	 * 
	 * @return session对应的隧道信息
	 */
	List<TunnelBO> listTunnelBos();

	/**
	 * 创建隧道
	 * 
	 * @param tunnelBO
	 *            隧道信息
	 */
	void createTunnel(TunnelBO tunnelBO);

	/**
	 * 创建隧道
	 * 
	 * @param tunnelBOs 隧道信息
	 */
	void createTunnels(List<TunnelBO> tunnelBOs);

	/**
	 * 释放所有的Session
	 */
	void releaseSessions();
}
