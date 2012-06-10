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

public class MessageQueryTO implements Serializable {

	/** Comment for <code>serialVersionUID</code>. */
	private static final long serialVersionUID = 9059555189195384313L;

	public static final String T_MESSAGE_QUERY = "M";
	public static final String T_QUERY = "Q";
	public static final String T_COUNT = "C";

	public int id = Constants.UNDEFINED_ID;
	public String shortname;
	public boolean shortNameDirty = false;
	public String name;
	public String type = T_MESSAGE_QUERY;
	public boolean nameDirty = false;
	public int iconId;
	public String sql;
	public boolean sqlDirty = false;
	public int count;

	public String toString() {
		return name + ":" + type + ":" + Integer.toString(count);
	}

}
