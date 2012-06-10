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
package ch.heftix.mailxel.client.to;

import java.io.Serializable;

public class AccountConfigTO implements Serializable {

	/** Comment for <code>serialVersionUID</code>. */
	private static final long serialVersionUID = -6192769216749611955L;

	public String name = null;
	public String protocol = "imap";
	public boolean isSSL = false;
	public String port = "143"; // # a1.port=993
	public String user = null;
	public String password = null;
	public String server = "mailserver.company.com";
	public String[] scannedfolders = new String[0];
	public String[] excludedfolders = new String[0];
	public boolean reuseSendPassword = false;
	public String[] reorgRules = new String[0];

	public String toString() {
		return name + ":" + protocol + ":" + server + ":" + port + ":" + user;
	}
}
