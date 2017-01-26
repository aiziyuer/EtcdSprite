package com.aiziyuer.app.ssh.biz;

import com.aiziyuer.app.ssh.bo.SshInfoBO;

public interface ISshInfoBiz {

	/**
	 * 查询所有的ssh连接信息
	 * 
	 * @return 返回所有的连接信息
	 */
	SshInfoBO queryAllSshInfoBos();

}
