/*
 * Copyright (C) 2008-2011 by Simon Hefti.
 * All rights reserved.
 * 
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * (see http://www.eclipse.org/legal/epl-v10.html)
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 */
package ch.heftix.mailxel.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.heftix.mailxel.client.ListUtil;
import ch.heftix.mailxel.client.to.Constants;
import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.xel.util.Util;

public class Mail {

	public int id = Constants.UNDEFINED_ID;
	public String uuid = null;
	public Date date = null;
	private List<String> from = new ArrayList<String>();
	private List<String> to = new ArrayList<String>();
	private List<String> cc = new ArrayList<String>();
	private List<String> bcc = new ArrayList<String>();
	public String subject = null;
	public String body = null;
	private AttachmentDescriptor attachmentDescriptor = null;
	public String inReplyTo = null;
	private List<String> references = new ArrayList<String>();

	public void addTo(final String address) {
		to.add(address);
	}

	public void addTo(final String[] address) {
		if (null == address) {
			return;
		}
		for (int i = 0; i < address.length; i++) {
			addTo(address[i]);
		}
	}

	public void addFrom(final String address) {
		from.add(address);
	}

	public void addFrom(final String[] address) {
		if (null == address) {
			return;
		}
		for (int i = 0; i < address.length; i++) {
			addFrom(address[i]);
		}
	}

	public void addCc(final String address) {
		cc.add(address);
	}

	public void addCc(final String[] address) {
		if (null == address) {
			return;
		}
		for (int i = 0; i < address.length; i++) {
			addCc(address[i]);
		}
	}

	public void addBcc(final String address) {
		bcc.add(address);
	}

	public void addBcc(final String[] address) {
		if (null == address) {
			return;
		}
		for (int i = 0; i < address.length; i++) {
			addBcc(address[i]);
		}
	}

	public void addReferences(final String[] references) {
		if (null == references) {
			return;
		}
		for (int i = 0; i < references.length; i++) {
			this.references.add(references[i]);
		}
	}

	public void setAttachmentDescriptor(final AttachmentDescriptor ad) {
		attachmentDescriptor = ad;
	}

	public AttachmentDescriptor getAttachmentDescriptor() {
		return attachmentDescriptor;
	}

	public String[] getFrom() {
		return ListUtil.asArray(from);
	}

	public String[] getTo() {
		return ListUtil.asArray(to);
	}

	public String[] getCc() {
		return ListUtil.asArray(cc);
	}

	public String[] getBcc() {
		return ListUtil.asArray(bcc);
	}

	public String[] getReferences() {
		return ListUtil.asArray(references);
	}

	public Envelope getEnvelope() {

		Envelope env = new Envelope();
		env.uuid = uuid;
		env.id = Constants.UNDEFINED_ID;
		env.subject = subject;
		env.to = ListUtil.asComaSeparted(to);
		env.from = ListUtil.asComaSeparted(from);
		env.cc = ListUtil.asComaSeparted(cc);
		env.date = Util.formatDate(date);

		return env;
	}
}
