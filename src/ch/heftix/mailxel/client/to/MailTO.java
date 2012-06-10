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

import java.util.ArrayList;
import java.util.List;

public class MailTO extends Envelope {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5383484263442925602L;
	
	public String body = null;
	public List<AttachmentTO> attachments = new ArrayList<AttachmentTO>();
	public List<AttachedCategoryTO> categories = new ArrayList<AttachedCategoryTO>();
	public AddressTO fromATO = null;
	public List<AddressTO> toATOs = new ArrayList<AddressTO>();
	public List<AddressTO> ccATOs = new ArrayList<AddressTO>();
	public List<AddressTO> bccATOs = new ArrayList<AddressTO>();
}
