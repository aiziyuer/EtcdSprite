package com.aiziyuer.app.ssh.dao;

import java.util.List;

import com.aiziyuer.app.ssh.po.SshInfoPO;

public interface ISshInfoDAO {
	/**
	 * 查询所有的ssh连接信息
	 * 
	 * @return 返回所有的连接信息
	 */
	List<SshInfoPO> listSshInfoPos();
}