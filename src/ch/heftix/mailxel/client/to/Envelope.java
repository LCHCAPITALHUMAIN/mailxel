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

public class Envelope implements Serializable {

	/** Comment for <code>serialVersionUID</code>. */
	private static final long serialVersionUID = 7475436749492800709L;

	public int id = Constants.UNDEFINED_ID;
	public String uuid;
	public String from;
	public String to;
	public String cc;
	public String bcc;
	public String date;
	public String time;
	public String subject;
	public String GTD; // text of current category (see curcatid)
	public String azip;
	public int nattach;
	public int curcatid;
	public int urgency = 0;
	public boolean fromMe = false;
	public boolean toMe = false;
	public int count = 0;

	public String toString() {
		return subject + ":" + from + ":" + to + ":" + Integer.toString(count);
	}
}
