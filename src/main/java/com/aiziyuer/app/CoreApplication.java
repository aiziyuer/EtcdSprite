package com.aiziyuer.app;

import org.eclipse.swt.widgets.Display;

public class CoreApplication {

	public static void main(String[] args) {
		
		try {
			MainApplicationWindow window = new MainApplicationWindow();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
