/*
 * Copyright (C) 2008-2011 by Simon Hefti. All rights reserved.
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * Initial Developer: Simon Hefti
 */
package ch.heftix.mailxel.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 */
public class Mailxel implements EntryPoint {

	/**
   */
	private final MailServiceAsync mailService = GWT.create(MailService.class);

	/**
   */
	public void onModuleLoad() {

		MailxelPanel mailxelPanel = new MailxelPanel(mailService);

		RootLayoutPanel.get().add(mailxelPanel);
	}

}
