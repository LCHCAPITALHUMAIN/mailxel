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

import java.util.List;

import ch.heftix.mailxel.client.to.AttachmentWithEnvelopes;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AttachmentOverviewGrid extends OverviewGrid<AttachmentWithEnvelopes> {
	
	public AttachmentOverviewGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {
		super(mailxelService, mailxelPanel);
		AttachmentWithEnvelopesPresenter presenter = new AttachmentWithEnvelopesPresenter();
		setPresenter(presenter);

		mailxelService.searchAttachment(null, new AsyncCallback<List<AttachmentWithEnvelopes>>() {

			public void onSuccess(List<AttachmentWithEnvelopes> result) {
				fillGrid(result);
			}

			public void onFailure(Throwable caught) {
				// show error

			}
		});
	}
	
}
