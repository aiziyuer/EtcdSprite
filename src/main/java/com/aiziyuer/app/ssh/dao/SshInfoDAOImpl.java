package com.aiziyuer.app.ssh.dao;

import com.aiziyuer.app.ssh.po.SshInfoPO;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.transaction.Transactional;
import java.util.List;

@Log4j2
@Transactional
public class SshInfoDAOImpl implements ISshInfoDAO {

    @Setter
    JdbcTemplate jdbcTemplate;

    @Setter
    NamedParameterJdbcTemplate namedTemplate;

    @Setter
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Override
    public SshInfoPO listSshInfoBos() {

        Session session = sessionFactory.getCurrentSession();

        List<SshInfoPO> sshInfoPOList = session.createQuery("from SshInfoPO").list();
        for (SshInfoPO sshInfoPO : sshInfoPOList) {
            System.out.println(sshInfoPO);
        }

        log.info("listSshInfoBos");


        return null;
    }

}
