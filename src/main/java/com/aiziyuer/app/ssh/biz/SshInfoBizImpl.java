package com.aiziyuer.app.ssh.biz;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;

import com.aiziyuer.app.framework.util.PathConstant;
import com.aiziyuer.app.framework.util.YamlUtils;
import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ssh.bo.TunnelBO;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoBizImpl implements ISshInfoBiz {

	private static final SshInfoBizImpl INSTANCE = new SshInfoBizImpl();

	private SshInfoBizImpl() {
	}

	public static ISshInfoBiz getInstance() {
		return INSTANCE;
	}

	/** Session的映射 */
	private Map<String, Session> sessionMap = new HashMap<String, Session>();

	/** ssh的连接信息 */
	private static final List<SessionInfoBO> SSH_INFO_BO_LIST;

	static {
		String yamlFile = Paths.get(PathConstant.DATA_DIR, String.format("%s.yml", SessionInfoBO.class.getSimpleName()))
				.toString();
		SSH_INFO_BO_LIST = YamlUtils.load(yamlFile);
	}

	@Override
	public List<SessionInfoBO> listSessionInfoBOs() {
		return SSH_INFO_BO_LIST;
	}

	@Override
	public void releaseSessions() {

		String yamlFile = Paths.get(PathConstant.DATA_DIR, String.format("%s.yml", SessionInfoBO.class.getSimpleName()))
				.toString();
		YamlUtils.save(yamlFile, SSH_INFO_BO_LIST);

		for (Session session : sessionMap.values()) {
			session.disconnect();
		}
	}

	@Override
	public void createTunnels(SessionInfoBO sessionInfoBO, List<TunnelBO> tunnelBOs) {

		for (TunnelBO tunnelBO : tunnelBOs)
			createTunnel(sessionInfoBO, tunnelBO);

	}

	@Override
	public void createTunnel(SessionInfoBO sessionInfoBO, TunnelBO tunnelBO) {

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

		return null;
	}

	@Override
	public List<TunnelBO> listTunnelBos() {
		return null;
	}

}
