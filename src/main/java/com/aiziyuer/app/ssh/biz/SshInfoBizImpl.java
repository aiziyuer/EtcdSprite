package com.aiziyuer.app.ssh.biz;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.aiziyuer.app.ssh.bo.SshInfoBO;
import com.aiziyuer.app.ssh.dao.ISshInfoDAO;
import com.aiziyuer.app.ssh.po.SshInfoPO;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoBizImpl implements ISshInfoBiz {

	@Setter
	private ISshInfoDAO sshInfoDAO;

	@Override
	public List<SshInfoBO> listSshInfoBOs() {

		List<SshInfoBO> sshInfoBOs = new ArrayList<SshInfoBO>();
		for (SshInfoPO sshInfoPO : sshInfoDAO.listSshInfoPos()) {
			SshInfoBO sshInfoBO = new SshInfoBO();
			try {
				BeanUtils.copyProperties(sshInfoBO, sshInfoPO);
				sshInfoBOs.add(sshInfoBO);
			} catch (IllegalAccessException | InvocationTargetException e) {
				log.error(e);
			}
		}

		return sshInfoBOs;
	}

	/**
	 * TODO 创建一条正向隧道<br>
	 * 
	 * 创建一条隧道(外部可以通过2202访问虚拟机的ssh服务), 使得外部可以访问虚拟机的80端口<br>
	 * <code>ssh -NfL 80:localhost:80 lc@127.0.0.1 -p 2202</code>
	 * 
	 */
	void createForwardTunnel() {

	}

	/**
	 * TODO 创建一条反向隧道<br>
	 * 创建一条反向隧道, 虚拟机可以访问外部的80端口服务<br>
	 * <code>ssh -NfR 8080:127.0.0.1:80 lc@127.0.0.1 -p 2202</code>
	 */
	void craeteReverseTunnel() {

	}

}
