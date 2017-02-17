package com.aiziyuer.app.ssh.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.aiziyuer.app.ssh.po.SessionInfoPO;
import com.aiziyuer.app.ssh.po.TunnelPO;

import lombok.Setter;

@SuppressWarnings("unchecked")
public class SshInfoDAOImpl implements ISshInfoDAO {

	@Setter
	JdbcTemplate jdbcTemplate;

	@Setter
	NamedParameterJdbcTemplate namedTemplate;

	@Setter
	private SessionFactory sessionFactory;

	@Override
	public List<SessionInfoPO> listSshInfoPos() {

		Session session = sessionFactory.getCurrentSession();

		return session.createQuery("from SessionInfoPO").list();
	}

	@Override
	public List<TunnelPO> listTunnelPos(long sessionInfoId) {

		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("select s FROM TunnelPO s WHERE SESSION_INFO_ID =:session_info_id");
		query.setParameter("session_info_id", sessionInfoId);

		return query.list();
	}

	@Override
	public List<TunnelPO> listTunnelPos() {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("SELECT s FROM TunnelPO s");

		return query.list();
	}

}
