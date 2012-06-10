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

import ch.heftix.mailxel.client.to.AttachmentWithEnvelopes;

import com.google.gwt.user.client.ui.FlexTable;

public class AttachmentWithEnvelopesPresenter implements Presenter<AttachmentWithEnvelopes> {

	public void setHeader(final FlexTable grid) {

		grid.setText(0, 0, "Name");
		grid.setText(0, 1, "Subject");
	}

	public int setRow(FlexTable grid, int row, AttachmentWithEnvelopes data) {

		grid.setText(row, 0, data.attachment.name);
		grid.setText(row, 1, data.envelopes.get(0).subject);

		return row;
	}

}
