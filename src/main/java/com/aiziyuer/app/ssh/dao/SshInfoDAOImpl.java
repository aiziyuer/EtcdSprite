package com.aiziyuer.app.ssh.dao;

import com.aiziyuer.app.ssh.po.SshInfoPO;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Log4j2
public class SshInfoDAOImpl implements ISshInfoDAO {

    @Setter
	JdbcTemplate jdbcTemplate;

    @Setter
    NamedParameterJdbcTemplate namedTemplate;

	@Override
	public SshInfoPO queryAllSshInfoBos() {

		log.info("queryAllSshInfoBos");

		return null;
	}

}
