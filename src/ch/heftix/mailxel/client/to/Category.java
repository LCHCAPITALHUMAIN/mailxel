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

public class Category implements Serializable {

	private static final long serialVersionUID = -1830152968431331655L;

	public int id = Constants.UNDEFINED_ID;
	public String shortname;
	public String name;
	public int iconid = Constants.UNDEFINED_ID;
	public int count = 0;
	public boolean deleted = false;

	public String toString() {
		return name + ":" + Integer.toString(id) + ":" + shortname + ":" + Integer.toString(count);
	}

}
