package com.aiziyuer.app.ui.ssh;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.databinding.observable.list.WritableList;

import com.aiziyuer.app.framework.common.CommonBO;
import com.aiziyuer.app.ssh.bo.SessionInfoBO;
import com.aiziyuer.app.ssh.bo.TunnelBO;
import com.aiziyuer.app.ui.common.AdvanceWritableList;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class SshInfoModle extends CommonBO {

	@Getter
	private WritableList input = new WritableList();
	


	@Getter
	@Setter
	@Accessors(bound = true)
	private SessionInfoBO sessionInfoBO;

	@Getter
	private AdvanceWritableList tunnels = new AdvanceWritableList();
	
	@Getter
	@Setter
	private TunnelBO tunnelBO;

	public SshInfoModle() {
		
		addPropertyChangeListener("sessionInfoBO", (PropertyChangeEvent event) -> {

			SessionInfoBO oldObj = (SessionInfoBO) event.getOldValue();
			SessionInfoBO newObj = (SessionInfoBO) event.getNewValue();
			if (newObj.equals(oldObj)) {
				return;
			}

			tunnels.replaceAll(newObj.getTunnelBOs());

		});

	}

}
