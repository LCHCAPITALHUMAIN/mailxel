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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestBox;

import ch.heftix.mailxel.client.to.Envelope;

public class CategorizationToolbar extends HorizontalPanel {

	private int currentMessageId = -1;
	private HasPrevNext prevNextProvider = null;
	private MailDetailDisplay mailDetailDisplay = null;

	public CategorizationToolbar(final HasPrevNext prevNextProvider, final MailDetailDisplay mailDetailDisplay,
			final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

		this.prevNextProvider = prevNextProvider;
		this.mailDetailDisplay = mailDetailDisplay;

		final AsyncCallback<Void> markCatCallback = new AsyncCallback<Void>() {

			public void onSuccess(Void result) {
				// mailxelPanel.setWorking(false);
				showNext();
				mailxelPanel.updateMailCounts();
			}

			public void onFailure(Throwable caught) {
				// mailxelPanel.setWorking(false);
				mailxelPanel.statusError("set categories failed", caught);
			}
		};

		Image fwd = new Image("img/resultset_next.png");
		fwd.setTitle("Next");
		fwd.setStylePrimaryName("mailxel-toolbar-item");
		fwd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				showNext();
			}
		});

		Image prev = new Image("img/resultset_previous.png");
		prev.setTitle("Next");
		prev.setStylePrimaryName("mailxel-toolbar-item");
		prev.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				showPrev();
			}
		});

		Image scheduleForLater = new Image("img/clock_go.png");
		scheduleForLater.setTitle("Schedule for Later");
		scheduleForLater.setStylePrimaryName("mailxel-toolbar-item");
		scheduleForLater.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					mailxelService.markStatus(currentMessageId, 999, markCatCallback);
				}
			}
		});

		// Image scheduleForToday = new Image("img/target.png");
		// scheduleForToday.setTitle("Schedule for Today");
		// scheduleForToday.setStylePrimaryName("mailxel-toolbar-item");
		// scheduleForToday.addClickHandler(new ClickHandler() {
		//
		// public void onClick(ClickEvent sender) {
		// if (currentMessageId > 0) {
		// mailxelService.markStatus(currentMessageId, 992, markCatCallback);
		// }
		// }
		// });

		Image imgLike = new Image("img/star-small.png");
		imgLike.setTitle("Like");
		imgLike.setStylePrimaryName("mailxel-toolbar-item");
		imgLike.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					mailxelService.markStatus(currentMessageId, 989, markCatCallback);
				}
			}
		});

		Image ignore = new Image("img/ignore.png");
		ignore.setTitle("Ignore");
		ignore.setStylePrimaryName("mailxel-toolbar-item");
		ignore.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					mailxelService.markStatus(currentMessageId, 994, markCatCallback);
				}
			}
		});

		Image tookNote = new Image("img/eye.png");
		tookNote.setTitle("Took Note");
		tookNote.setStylePrimaryName("mailxel-toolbar-item");
		tookNote.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					mailxelService.markStatus(currentMessageId, 993, markCatCallback);
				}
			}
		});

		Image waitForAnswer = new Image("img/hourglass.png");
		waitForAnswer.setTitle("waiting for answer");
		waitForAnswer.setStylePrimaryName("mailxel-toolbar-item");
		waitForAnswer.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					mailxelService.markStatus(currentMessageId, 998, markCatCallback);
				}
			}
		});

		Image done = new Image("img/tick.png");
		done.setTitle("task completed");
		done.setStylePrimaryName("mailxel-toolbar-item");
		done.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					mailxelService.markStatus(currentMessageId, 997, markCatCallback);
				}
			}
		});

		Image trash = new Image("img/bin.png");
		trash.setTitle("trash");
		trash.setStylePrimaryName("mailxel-toolbar-item");
		trash.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentMessageId > 0) {
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(currentMessageId);
					mailxelService.markAsDeleted(ids, markCatCallback);
				}
			}
		});

		CategorySuggestOracle oracle = new CategorySuggestOracle(mailxelService);
		final SuggestBox gtdSb = new SuggestBox(oracle);
		gtdSb.setWidth("50px");
		gtdSb.setTitle("add category");

		Image categorize = new Image("img/categorize.png");
		categorize.setTitle("categorize");
		categorize.setStylePrimaryName("mailxel-toolbar-item");
		categorize.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				String gtd = gtdSb.getText();
				if (currentMessageId > 0 && null != gtd) {
					List<Integer> ids = new ArrayList<Integer>();
					ids.add(currentMessageId);
					mailxelService.updateCategories(ids, gtd, markCatCallback);
				}
			}
		});

		HorizontalPanel gtdBar = new HorizontalPanel();
		gtdBar.add(gtdSb);
		gtdBar.add(categorize);

		add(prev);
		add(scheduleForLater);
//		add(scheduleForToday);
		add(imgLike);
		add(waitForAnswer);
		add(done);
		add(tookNote);
		add(ignore);
		add(trash);
		add(gtdBar);
		add(fwd);
	}

	public void setCurrentMessageId(final int currentMessageId) {
		this.currentMessageId = currentMessageId;
	}

	private void showNext() {
		Envelope env = prevNextProvider.next();
		mailDetailDisplay.displayMailDetail(env.id);
	}

	private void showPrev() {
		Envelope env = prevNextProvider.prev();
		mailDetailDisplay.displayMailDetail(env.id);
	}
}
