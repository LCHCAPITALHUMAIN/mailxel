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

public class IconTO implements Serializable {

	/** Comment for <code>serialVersionUID</code>. */
	private static final long serialVersionUID = -7911720610236344947L;

	public int id = Constants.UNDEFINED_ID;
	public String name;

	public String toString() {
		return name;
	}

}
