package com.aiziyuer.app.ssh.dao;

import java.util.List;

import com.aiziyuer.app.ssh.po.SessionInfoPO;
import com.aiziyuer.app.ssh.po.TunnelPO;

public interface ISshInfoDAO {
	/**
	 * 查询所有的ssh连接信息
	 * 
	 * @return 返回所有的连接信息
	 */
	List<SessionInfoPO> listSshInfoPos();

	/**
	 * 查询隧道信息
	 * 
	 * @param sessionInfoId
	 *            session的标示
	 * 
	 * @return session对应的隧道信息
	 */
	List<TunnelPO> listTunnelPos(long sessionInfoId);

	/**
	 * 查询隧道信息
	 * 
	 * @return session对应的隧道信息
	 */
	List<TunnelPO> listTunnelPos();
}
