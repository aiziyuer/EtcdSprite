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
			sshInfoBO.addObserver(sshInfoBO);
			try {
				BeanUtils.copyProperties(sshInfoBO, sshInfoPO);
				sshInfoBOs.add(sshInfoBO);
			} catch (IllegalAccessException | InvocationTargetException e) {
				log.error(e);
			}
		}

		return sshInfoBOs;
	}

}
