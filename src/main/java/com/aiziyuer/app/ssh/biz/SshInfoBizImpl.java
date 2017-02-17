package com.aiziyuer.app.ssh.biz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ssh.bo.TunnelBO;
import com.aiziyuer.app.ssh.dao.ISshInfoDAO;
import com.aiziyuer.app.ssh.po.SessionInfoPO;
import com.aiziyuer.app.ssh.po.TunnelPO;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoBizImpl implements ISshInfoBiz {

	@Setter
	private ISshInfoDAO sshInfoDAO;

	@Override
	public List<SessionInfoBO> listSessionInfoBOs() {

		List<SessionInfoBO> sshInfoBOs = new ArrayList<SessionInfoBO>();
		for (SessionInfoPO sshInfoPO : sshInfoDAO.listSshInfoPos()) {
			SessionInfoBO sshInfoBO = new SessionInfoBO();
			BeanUtils.copyProperties(sshInfoPO, sshInfoBO);
			sshInfoBOs.add(sshInfoBO);
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

	@Override
	public List<TunnelBO> listTunnelBos(long sessionInfoId) {

		log.info(String.format("sessionInfoId:%d", sessionInfoId));

		return convertTunnelPos2TunnelBos(sshInfoDAO.listTunnelPos(sessionInfoId));
	}

	@Override
	public List<TunnelBO> listTunnelBos() {
		return convertTunnelPos2TunnelBos(sshInfoDAO.listTunnelPos());
	}

	/**
	 * 将隧道PO类型转换为隧道BO类型
	 * 
	 * @param tunnelPOs
	 *            隧道PO类型
	 * @return 隧道信息
	 */
	private List<TunnelBO> convertTunnelPos2TunnelBos(List<TunnelPO> tunnelPOs) {

		List<TunnelBO> tunnelBOs = new ArrayList<TunnelBO>();

		for (TunnelPO tunnelPO : tunnelPOs) {
			TunnelBO tunnelBO = new TunnelBO();
			SessionInfoBO sessionInfoBO = new SessionInfoBO();
			tunnelBO.setSessionInfo(sessionInfoBO);
			BeanUtils.copyProperties(tunnelPO.getSessionInfoPO(), sessionInfoBO);
			BeanUtils.copyProperties(tunnelPO, tunnelBO);
			tunnelBOs.add(tunnelBO);
		}

		return tunnelBOs;
	}

}
