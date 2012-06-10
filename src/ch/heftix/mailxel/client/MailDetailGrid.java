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

import ch.heftix.mailxel.client.to.AttachedCategoryTO;
import ch.heftix.mailxel.client.to.AttachmentTO;
import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.mailxel.client.to.MailTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MailDetailGrid extends VerticalPanel implements MailDetailDisplay, HasMailxelTab {

	private MailServiceAsync mailxelService = null;
	private FlexTable grid = null;
	private TextArea bodyArea = null;
	private FlexTable categoryHistory = null;
	private MailTO mailTO = null;
	private AttachmentBar attachementBar = null;
	private MailxelPanel mailxelPanel = null;
	private CategorizationToolbar categorizationToolbar = null;
	private Image fwd = null;
	private Image reply = null;
	private Image replyAll = null;
	private MailxelTab mailxelTab = null;
	// private TabPanel bodyPanel = null;
	private OnDemandTabPanel contentTabPanel = null;
	private ConfigTO cTO = null;

	public MailDetailGrid(final HasPrevNext prevNextProvider, final MailServiceAsync mailxelService,
			final MailxelPanel mailxelPanel) {

		this.mailxelService = mailxelService;
		this.mailxelPanel = mailxelPanel;
		this.cTO = mailxelPanel.getConfig();

		categorizationToolbar = new CategorizationToolbar(prevNextProvider, this, mailxelService, mailxelPanel);

		fwd = new Image("img/mail-forward.png");
		fwd.setTitle("Forward");
		fwd.setStylePrimaryName("mailxel-toolbar-item");
		fwd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				final MailSendGrid mailSendGrid = new MailSendGrid(mailxelService, mailxelPanel, mailTO,
						MailSendGrid.TYPE_FORWARD);
				String title = "Fwd: " + mailTO.subject;
				mailxelPanel.addTab(mailSendGrid, title);
			}
		});

		reply = new Image("img/mail-reply.png");
		reply.setTitle("Reply");
		reply.setStylePrimaryName("mailxel-toolbar-item");
		reply.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				final MailSendGrid mailSendGrid = new MailSendGrid(mailxelService, mailxelPanel, mailTO,
						MailSendGrid.TYPE_REPLY);
				String title = "Re: " + mailTO.subject;
				mailxelPanel.addTab(mailSendGrid, title);
			}
		});

		replyAll = new Image("img/mail-reply-all.png");
		replyAll.setTitle("Reply All");
		replyAll.setStylePrimaryName("mailxel-toolbar-item");
		replyAll.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				final MailSendGrid mailSendGrid = new MailSendGrid(mailxelService, mailxelPanel, mailTO,
						MailSendGrid.TYPE_REPLY_ALL);
				String title = "Re: " + mailTO.subject;
				mailxelPanel.addTab(mailSendGrid, title);
			}
		});

		initWidgets();

	}

	private void initWidgets() {

		clear();

		// bodyPanel = new TabPanel();
		grid = new FlexTable();
		categoryHistory = new FlexTable();
		attachementBar = new AttachmentBar(mailxelService);
		bodyArea = new OrgTextArea();
		bodyArea.setCharacterWidth(80);
		bodyArea.setVisibleLines(25);
		bodyArea.setReadOnly(true);

		HorizontalPanel toolbar = new HorizontalPanel();

		toolbar.add(fwd);
		toolbar.add(replyAll);
		toolbar.add(reply);
		toolbar.add(new Label(" "));
		toolbar.add(categorizationToolbar);
		toolbar.add(new Label(" "));

		add(toolbar);
		add(grid);

		contentTabPanel = new OnDemandTabPanel();
		contentTabPanel.add(bodyArea, "mail");
		add(contentTabPanel);

		add(categoryHistory);

	}

	public void displayMailDetail(final int id) {

		mailxelService.get(id, new AsyncCallback<MailTO>() {

			final StatusItem si = mailxelPanel.statusStart("getting mail detail");

			public void onSuccess(MailTO result) {

				categorizationToolbar.setCurrentMessageId(result.id);

				if (null != mailxelTab) {
					mailxelTab.setTabText(result.subject);
				}

				initWidgets();

				AddressLabel lFrom = new AddressLabel(result.fromATO);

				grid.setText(0, 0, "From");
				grid.setWidget(0, 1, lFrom);

				AddressBar toBar = new AddressBar();
				toBar.add(result.toATOs);

				grid.setText(1, 0, "To");
				grid.setWidget(1, 1, toBar);

				int cursor = 2;

				if (null != result.ccATOs && result.ccATOs.size() > 0) {
					grid.setText(cursor, 0, "Cc");
					AddressBar ccBar = new AddressBar();
					ccBar.add(result.ccATOs);
					grid.setWidget(cursor, 1, ccBar);
					cursor++;
				}
				if (null != result.bccATOs && result.bccATOs.size() > 0) {
					grid.setText(cursor, 0, "Bcc");
					AddressBar bccBar = new AddressBar();
					bccBar.add(result.bccATOs);
					grid.setWidget(cursor, 1, bccBar);
					cursor++;
				}

				// date
				grid.setText(cursor, 0, "Date");
				String zDate = result.date;
				if (null != result.time && null != cTO && cTO.displayTime) {
					zDate = zDate + " " + result.time;
				}
				zDate = zDate + " (access count: " + Integer.toString(result.count) + ")";
				Label lDate = new Label();
				lDate.setText(zDate);
				lDate.setTitle(result.time);

				grid.setWidget(cursor, 1, lDate);
				cursor++;

				grid.setText(cursor, 0, "Subject");
				grid.setText(cursor, 1, result.subject);
				cursor++;

				bodyArea.setText(result.body);
				contentTabPanel.add(bodyArea);

				if (null != result.attachments && result.attachments.size() > 0) {
					grid.setText(cursor, 0, "A");
					grid.setWidget(cursor, 1, attachementBar);
					cursor++;

					for (AttachmentTO attachement : result.attachments) {
						attachementBar.addAttachement(attachement, mailxelPanel, contentTabPanel);
					}

				}

				mailTO = result;

				cursor = 0;

				if (null != result.categories && result.categories.size() > 0) {
					for (AttachedCategoryTO aCat : result.categories) {
						categoryHistory.setText(cursor, 0, "C");
						categoryHistory.setText(cursor, 1, aCat.date);
						categoryHistory.setText(cursor, 2, aCat.category.name);
						cursor++;
					}
				}

				si.done();
			}

			public void onFailure(Throwable caught) {
				si.error(caught);
			}

		});
	}

	/**
	 * set tab widget - used to set tab description later on, e.g. when moving
	 * through a resultset with the CategorizationToolbar
	 */
	public void setMailxelTab(final MailxelTab mailxelTab) {
		this.mailxelTab = mailxelTab;
	}

}
