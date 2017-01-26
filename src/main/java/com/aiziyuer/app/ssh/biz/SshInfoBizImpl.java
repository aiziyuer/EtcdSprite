package com.aiziyuer.app.ssh.biz;

import com.aiziyuer.app.ssh.bo.SshInfoBO;
import com.aiziyuer.app.ssh.dao.ISshInfoDAO;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SshInfoBizImpl implements ISshInfoBiz {

    @Setter
    private ISshInfoDAO sshInfoDAO;

    @Override
    public SshInfoBO listSshInfoBos() {

        log.info("listSshInfoBos");

        sshInfoDAO.listSshInfoBos();

        return null;
    }

}
