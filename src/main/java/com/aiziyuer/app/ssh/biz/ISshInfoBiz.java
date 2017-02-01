package com.aiziyuer.app.ssh.biz;

import java.util.List;

import com.aiziyuer.app.ssh.bo.SshInfoBO;

public interface ISshInfoBiz {

	/**
	 * 查询所有的ssh连接信息
	 * 
	 * @return 返回所有的连接信息
	 */
	List<SshInfoBO> listSshInfoBOs();

}
