package com.aiziyuer.app.ssh.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.aiziyuer.app.ssh.po.SshInfoPO;

import lombok.Setter;

public class SshInfoDAOImpl implements ISshInfoDAO {

	@Setter
	JdbcTemplate jdbcTemplate;

	@Setter
	NamedParameterJdbcTemplate namedTemplate;

	@Setter
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<SshInfoPO> listSshInfoPos() {

		Session session = sessionFactory.getCurrentSession();

		return session.createQuery("from SshInfoPO").list();
	}

}
