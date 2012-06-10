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

import ch.heftix.mailxel.client.to.AttachmentTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AttachmentPanel extends VerticalPanel {

	public AttachmentPanel(final AttachmentTO attachmentTO,
			final MailServiceAsync mailxelService,
			final MailxelPanel mailxelPanel, final boolean directDownload) {

		String url = GWT.getModuleBaseURL() + "mailxel?id="
				+ Integer.toString(attachmentTO.id);
		int type = MimeTypeDescriptor.TYPE_ANY;

		final MimeTypeDescriptor mtd = MimeTypeUtil
				.mimeTypeByName(attachmentTO.name);
		type = mtd.fileType;

		Frame frame = null;

		if (directDownload) {
			frame = new Frame(url);
			add(frame);
			return;
		}

		ServiceCall sc = new ServiceCall() {

			public String getMessage() {
				return "get attachment data";
			};

			public boolean runSynchronized() {
				return true;
			};

			public void run(final StatusItem si) {
				mailxelService.getAttachmentData(attachmentTO.id,
						new AsyncCallback<String>() {

							public void onSuccess(String result) {
								si.done();
								switch (mtd.fileType) {
								case MimeTypeDescriptor.TYPE_HTM:
									HTML html = new HTML();
									html.setHTML(result);
									add(html);
									break;

								default:
									TextArea ta = new TextArea();
									ta.setCharacterWidth(80);
									ta.setVisibleLines(25);
									ta.setReadOnly(true);
									ta.setText(result);
									add(ta);
									break;
								}
							}

							public void onFailure(Throwable caught) {
								si.error(caught);
							}
						});
			}
		};

		switch (type) {
		case MimeTypeDescriptor.TYPE_IMG:
			Image img = new Image(url);
			add(img);
			break;

		case MimeTypeDescriptor.TYPE_HTM:
		case MimeTypeDescriptor.TYPE_TXT:

			mailxelPanel.statusStart(sc);
			break;

		default:
			frame = new Frame(url);
			add(frame);
			break;
		}
	}
}
