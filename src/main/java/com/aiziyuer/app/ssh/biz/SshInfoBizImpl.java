package com.aiziyuer.app.ssh.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;

import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ssh.bo.TunnelBO;
import com.aiziyuer.app.ssh.dao.ISshInfoDAO;
import com.aiziyuer.app.ssh.po.SessionInfoPO;
import com.aiziyuer.app.ssh.po.TunnelPO;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoBizImpl implements ISshInfoBiz {

	@Setter
	private ISshInfoDAO sshInfoDAO;

	/** Session的映射 */
	private Map<String, Session> sessionMap = new HashMap<String, Session>();

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

	@Override
	public void releaseSessions() {
		for (Session session : sessionMap.values()) {
			session.disconnect();
		}
	}

	@Override
	public void createTunnels(List<TunnelBO> tunnelBOs) {

		for (TunnelBO tunnelBO : tunnelBOs)
			createTunnel(tunnelBO);

	}

	@Override
	public void createTunnel(TunnelBO tunnelBO) {

		SessionInfoBO sessionInfoBO = tunnelBO.getSessionInfo();
		if (sessionInfoBO == null) {
			log.error(String.format("tunnelBO:(%s) has no sessionInfo", tunnelBO));
			return;
		}

		String name = sessionInfoBO.getUserName();
		String host = sessionInfoBO.getHost();
		int port = sessionInfoBO.getPort();
		String keyStr = String.format("%s@%s:%d", name, host, port);
		
		Session session = sessionMap.get(keyStr);
		if (session == null) {
			JSch jsch = new JSch();
			try {
				session = jsch.getSession(name, host, port);

				// 设置不检查hostKey
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);

				// 设置密码
				session.setPassword(sessionInfoBO.getUserPassword());

				// 设置超时
				int timeout = 3000;
				session.connect(timeout);

				log.info(String.format("create session(%s) success.", keyStr));

				sessionMap.put(keyStr, session);
			} catch (JSchException e) {
				log.error("create session error:", e);
			}
		}

		if (tunnelBO.isLocal())
			createForwardTunnel(session, tunnelBO);
		else
			craeteReverseTunnel(session, tunnelBO);
	}

	/**
	 * 创建一条正向隧道<br>
	 * 
	 * 创建一条隧道(外部可以通过2202访问虚拟机的ssh服务), 使得外部可以访问虚拟机的80端口<br>
	 * <code>ssh -NfL 80:localhost:80 lc@127.0.0.1 -p 2202</code>
	 * 
	 */
	private void createForwardTunnel(Session session, TunnelBO tunnelBO) {

		// 隧道入口
		String localHost = tunnelBO.getLocalTunnelHost();
		int localPort = NumberUtils.toInt(tunnelBO.getLocalTunnelPort());

		// 隧道出口
		String remoteHost = tunnelBO.getRemoteTunnelHost();
		int remotePort = NumberUtils.toInt(tunnelBO.getRemoteTunnelPort());

		try {
			int assinged_port = session.setPortForwardingL(localHost, localPort, remoteHost, remotePort);
			log.info(String.format("ForwardTunnel: local(%s:%d) -> remote(%s:%d)", localHost, assinged_port, remoteHost,
					remotePort));
		} catch (JSchException e) {
			log.error("create tunnel error:", e);
		}
	}

	/**
	 * 创建一条反向隧道<br>
	 * 创建一条反向隧道, 虚拟机可以访问外部的80端口服务<br>
	 * <code>ssh -NfR 8080:localhost:80 lc@127.0.0.1 -p 2202</code>
	 */
	private void craeteReverseTunnel(Session session, TunnelBO tunnelBO) {

		// 隧道入口
		String localHost = tunnelBO.getLocalTunnelHost();
		int localPort = NumberUtils.toInt(tunnelBO.getLocalTunnelPort());

		// 隧道出口
		String remoteHost = tunnelBO.getRemoteTunnelHost();
		int remotePort = NumberUtils.toInt(tunnelBO.getRemoteTunnelPort());

		try {
			session.setPortForwardingR(remoteHost, remotePort, localHost, localPort);
			log.info(String.format("ReverseTunnel: local(%s:%d) <- remote(%s:%d)", localHost, localPort, remoteHost,
					remotePort));
		} catch (JSchException e) {
			log.error("create tunnel error:", e);
		}
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
