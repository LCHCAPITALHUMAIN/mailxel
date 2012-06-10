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

import ch.heftix.mailxel.client.to.Constants;

import com.google.gwt.core.client.GWT;

/**
 * Helper. Provides static helper methods.
 */
public class DirectMailServiceUtil {

	public static String getIconURL(final int id) {
		if (Constants.UNDEFINED_ID == id) {
			return null;
		}
		String url = GWT.getModuleBaseURL() + "mailxel?img=" + Integer.toString(id);
		return url;
	}
}
