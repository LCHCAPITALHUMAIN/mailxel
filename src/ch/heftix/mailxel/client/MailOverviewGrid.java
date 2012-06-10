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

import ch.heftix.mailxel.client.to.AttachedCategoryTO;
import ch.heftix.mailxel.client.to.AttachmentTO;
import ch.heftix.mailxel.client.to.Envelope;
import ch.heftix.mailxel.client.to.IconTO;
import ch.heftix.mailxel.client.to.MailTO;
import ch.heftix.mailxel.client.to.MessageSearchTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MailOverviewGrid extends VerticalPanel implements HasPrevNext {

	private List<Envelope> envelopes = new ArrayList<Envelope>();
	// private List<CheckBox> checkboxes = new ArrayList<CheckBox>();

	private CursoredList<Envelope> cl = null;

	private FlexTable grid = new FlexTable();
	private RowFormatter rf = null;
	private HorizontalPanel searchPanel = new HorizontalPanel();

	public static final int LABEL_ROW = 0;

	private TextBox fts = null;

	private int currentPage = 0;

	private int first_payload_row = 1;

	public static final int C_SELECT = 0;
	public static final int C_FROM = 1;
	public static final int C_TO = 2;
	public static final int C_DATE = 3;
	public static final int C_GTD = 4;
	public static final int C_ATTACHMENT = 5;
	public static final int C_SUBJECT = 6;

	private MailServiceAsync mailxelService = null;

	public MailOverviewGrid(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

		init(false, mailxelService, mailxelPanel);

	}

	public MailOverviewGrid(final boolean withoutSearch, final MailServiceAsync mailxelService,
			final MailxelPanel mailxelPanel) {

		init(withoutSearch, mailxelService, mailxelPanel);

	}

	private void init(final boolean withoutSearch, final MailServiceAsync mailxelService,
			final MailxelPanel mailxelPanel) {

		this.mailxelService = mailxelService;

		rf = grid.getRowFormatter();

		// header
		grid.setText(LABEL_ROW, C_FROM, "From");
		grid.setText(LABEL_ROW, C_TO, "To");
		grid.setText(LABEL_ROW, C_DATE, "Date");
		grid.setText(LABEL_ROW, C_GTD, "GTD");
		grid.setText(LABEL_ROW, C_ATTACHMENT, "A");
		// subjectPanel.add(new Label("Subject"));
		// grid.setWidget(LABEL_ROW, C_SUBJECT, subjectPanel);
		grid.setText(LABEL_ROW, C_SUBJECT, "Subject");

		ColumnFormatter fmt = grid.getColumnFormatter();

		fmt.setWidth(C_FROM, "75px");
		fmt.setWidth(C_TO, "75px");
		fmt.setWidth(C_DATE, "75px");
		fmt.setWidth(C_GTD, "50px");
		fmt.setWidth(C_ATTACHMENT, "10px");
		fmt.setWidth(C_SUBJECT, "400px");

		final KeyPressHandler kbla = new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent keyPressEvent) {
				if (KeyCodes.KEY_ENTER == keyPressEvent.getCharCode()) {
					currentPage = 0;
					startMessageSearch(mailxelService, mailxelPanel);
				}
			}
		};

		fts = new TextBox();
		fts.setTitle("full text search");
		fts.setWidth("700px");
		fts.addKeyPressHandler(kbla);

		final Image nextPage = new Image("img/resultset_next.png");
		nextPage.setTitle("Next Page");
		nextPage.setStylePrimaryName("mailxel-toolbar-item");
		nextPage.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				currentPage++;
				startMessageSearch(mailxelService, mailxelPanel);
			}
		});

		final Image prevPage = new Image("img/resultset_previous.png");
		prevPage.setTitle("Previous Page");
		prevPage.setStylePrimaryName("mailxel-toolbar-item");
		prevPage.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				if (currentPage > 0) {
					currentPage--;
				}
				startMessageSearch(mailxelService, mailxelPanel);
			}
		});

		// paginatePanel.add(attachmentName);
		searchPanel.add(fts);
		searchPanel.add(prevPage);
		searchPanel.add(nextPage);

		grid.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent clickEvent) {

				Cell cell = grid.getCellForEvent(clickEvent);
				if (null == cell) {
					return;
				}
				final int row = cell.getRowIndex();
				final int col = cell.getCellIndex();
				// row >= FIRST_PAYLOAD_ROW: skip header
				// col > 0: skip first col; contains a checkbox with separate
				// listener

				if (row >= first_payload_row && col > 0) {

					Envelope env = envelopes.get(row - first_payload_row);

					if (col == C_ATTACHMENT && env.nattach > 0) {

						// show popup with attachment details
						mailxelService.getAttachments(env.id, new AsyncCallback<List<AttachmentTO>>() {

							public void onSuccess(List<AttachmentTO> result) {

								PopupPanel pop = new PopupPanel(true);
								AttachmentBar ab = new AttachmentBar(mailxelService);
								for (AttachmentTO aTO : result) {
									ab.addAttachement(aTO, mailxelPanel, null);
								}
								pop.add(ab);
								Widget tmp = (Image) grid.getWidget(row, col);
								int x = tmp.getAbsoluteLeft();
								int y = tmp.getAbsoluteTop();
								pop.setPopupPosition(x, y);
								pop.show();
							}

							public void onFailure(Throwable caught) {
								// ignore
							}
						});

					} else if (991 == env.curcatid) {

						// edit message
						rf.setStylePrimaryName(row, "row-selected");

						MailTO mt = new MailTO();
						final MailSendGrid sg = new MailSendGrid(mailxelService, mailxelPanel, mt,
								MailSendGrid.TYPE_NEW);
						sg.setMailDetail(env.id);
						mailxelPanel.addTab(sg, env.from + ": " + env.subject);

					} else {

						// open a detail tab

						rf.setStylePrimaryName(row, "row-selected");

						cl.setCursorPosition(row - first_payload_row);

						addDetailTab(mailxelService, mailxelPanel, env);
					}

				}
			}
		});

		if (!withoutSearch) {
			add(searchPanel);
		}

		ScrollPanel sp = new ScrollPanel();
		sp.add(grid);
		add(sp);
	}

	/**
	 * create a label and add it at row,col to the flextable. Shorten text for
	 * normal display and unshortened text for setTitle, i.e. mouse-over
	 * tool-tip.
	 * 
	 * @param shortenTo
	 *            avoid shortening if <0
	 */
	public Label setTextHelper(final FlexTable grid, final int row, final int col, final String text,
			final int shortenTo) {

		Label label = createLabel(text, shortenTo);
		grid.setWidget(row, col, label);
		return label;
	}

	/**
	 * create a label. Shorten text for normal display and unshortened text for
	 * setTitle, i.e. mouse-over tool-tip.
	 * 
	 * @param shortenTo
	 *            avoid shortening if <0
	 */
	public Label createLabel(final String text, final int shortenTo) {
		Label label = new Label();
		if (shortenTo >= 0 && null != text && text.length() > shortenTo) {
			label.setText(UIUtil.shorten(text, shortenTo, ".."));
			label.setTitle(text);
		} else {
			label.setText(text);
		}
		return label;
	}

	/**
	 * create a label, display date, add time as tool tip
	 */
	public Label dateTimeLabel(final String date, final String time) {
		Label label = new Label();
		label.setText(date);
		label.setTitle(time);
		return label;
	}

	private void startMessageSearch(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

		final MessageSearchTO msTO = new MessageSearchTO();

		msTO.fts = UIUtil.trimNull(fts.getText());

		ServiceCall sc = new ServiceCall() {

			public String getMessage() {
				return "message search";
			};

			public boolean runSynchronized() {
				return true;
			};

			public void run(final StatusItem si) {
				mailxelService.select(currentPage, msTO, new AsyncCallback<List<Envelope>>() {

					public void onSuccess(List<Envelope> result) {

						si.done("found " + Integer.toString(result.size()) + " messages.", 0);
						fillGrid(result);
					}

					public void onFailure(Throwable caught) {
						si.error(caught);
					}
				});
			}
		};

		mailxelPanel.statusStart(sc);
		// final StatusItem si = mailxelPanel.statusStart("message search");

	}

	private void addDetailTab(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel, Envelope env) {
		final MailDetailGrid mailDetailGrid = new MailDetailGrid(this, mailxelService, mailxelPanel);
		mailDetailGrid.displayMailDetail(env.id);
		mailxelPanel.addTab(mailDetailGrid, env.from + ": " + env.subject);
	}

	public Envelope next() {
		Envelope env = cl.next();
		return env;
	}

	public Envelope prev() {
		Envelope env = cl.prev();
		return env;
	}

	public void fillGrid(List<Envelope> result) {

		envelopes = result;
		// checkboxes = new ArrayList<CheckBox>(envelopes.size());

		// clean all except header
		int rows = grid.getRowCount();
		for (int i = rows - 1; i >= first_payload_row; i--) {
			grid.removeRow(i);
		}

		int row = first_payload_row;

		for (final Envelope envelope : result) {

			grid.setText(row, C_SELECT, Integer.toString(envelope.count));

			setTextHelper(grid, row, C_FROM, envelope.from, 12);
			setTextHelper(grid, row, C_TO, envelope.to, 12);
			Label dateTime = dateTimeLabel(envelope.date, envelope.time);
			if (envelope.urgency > 0) {
				String style = "background-color:#FA5858;";
				switch (envelope.urgency) {
				case 4:
					style = "background-color:#FA5858;";
					break;
				case 3:
					style = "background-color:#FAAC58;";
					break;
				case 2:
					style = "background-color:#F4FA58;";
					break;
				default:
					style = "background-color:#ACFA58;";
					break;
				}
				DOM.setElementAttribute(dateTime.getElement(), "style", style);
			}

			grid.setWidget(row, C_DATE, dateTime);

			// GTD label (unless label is 'not categorized')
			if (990 != envelope.curcatid) {
				Label gtdLabel = createLabel(envelope.GTD, 16);
				grid.setWidget(row, C_GTD, gtdLabel);
				// see if there is an icon for it
				Image img = setIconForGTD(grid, row, envelope.curcatid, envelope.GTD);
				img.addMouseOverHandler(new MouseOverHandler() {
					public void onMouseOver(final MouseOverEvent event) {

						final PopupPanel pop = new PopupPanel(true);
						pop.setPopupPosition(event.getClientX(), event.getClientY());

						mailxelService.getCategoryHistory(envelope.id, new AsyncCallback<List<AttachedCategoryTO>>() {
							public void onFailure(Throwable caught) {
								//
							}

							public void onSuccess(List<AttachedCategoryTO> result) {
								if (null == result || result.size() < 1) {
									return;
								}
								FlexTable ft = new FlexTable();
								pop.add(ft);
								int row = 0;
								for (AttachedCategoryTO cat : result) {
									ft.setText(row, 0, cat.date);
									String url = DirectMailServiceUtil.getIconURL(cat.category.iconid);
									if (null != url) {
										Image img = new Image(url);
										ft.setWidget(row, 1, img);
									}
									ft.setText(row, 2, cat.category.name);
									row++;
								}
								pop.show();
							}

						});
					}
				});
			}

			if (envelope.nattach > 0) {
				Image attach = new Image("img/attach.png");
				grid.setWidget(row, C_ATTACHMENT, attach);
			}
			Label subject = setTextHelper(grid, row, C_SUBJECT, envelope.subject, 64);
			// subject.addMouseOverHandler(new MouseOverHandler() {
			//
			// public void onMouseOver(MouseOverEvent event) {
			// // System.out.println("on mouse over:" + event + "/" +
			// // envelope);
			// mailxelService.snippet(envelope.id,
			// new AsyncCallback<String>() {
			//
			// public void onFailure(Throwable caught) {
			// System.out.println("E snippet: " + caught);
			// }
			//
			// public void onSuccess(String result) {
			// System.out.println("I snippet: " + result);
			//
			// }
			// });
			// }
			// });

			if (row % 2 == 0) {
				rf.setStylePrimaryName(row, "row-bg");
			}
			row++;

		}

		cl = new CursoredList<Envelope>(envelopes);
		cl.setCursorPosition(0);
	}

	protected Image setIconForGTD(final FlexTable ft, final int row, final int categoryId, final String toolTip) {
		final Image res = new Image();
		mailxelService.getIconForCategory(categoryId, new AsyncCallback<IconTO>() {
			public void onSuccess(IconTO result) {
				String url = DirectMailServiceUtil.getIconURL(result.id);
				if (null != url) {
					res.setUrl(url);
					res.setTitle(toolTip);
					ft.setWidget(row, C_GTD, res);
				}
			}

			public void onFailure(Throwable caught) {
				// ignore
			}
		});
		return res;
	}
}
