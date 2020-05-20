package com.fo0.vaadin.scrumtool.session;

import com.fo0.vaadin.scrumtool.utils.Utils;
import com.vaadin.flow.server.VaadinSession;

public class SessionUtils {

	public static void createSessionIdIfExists() {
		if (getSessionId() == null) {
			VaadinSession.getCurrent().setAttribute("session-id", Utils.randomId());
		}
	}
	
	public static String getSessionId() {
		return (String) VaadinSession.getCurrent().getAttribute("session-id");
	}

}
