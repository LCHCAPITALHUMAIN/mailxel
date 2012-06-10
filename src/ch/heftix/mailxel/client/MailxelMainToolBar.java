/*
 * Licensed under the EPL 1.0 (Eclipse Public License).
 * Copyright (C) 2008-2011 by Simon Hefti. All rights reserved.
 * (see http://www.eclipse.org/legal/epl-v10.html)
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * Initial Developer: Simon Hefti
 */
package ch.heftix.mailxel.client;

import java.util.List;

import ch.heftix.mailxel.client.to.ConfigTO;
import ch.heftix.mailxel.client.to.MessageQueryTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class MailxelMainToolBar extends HorizontalPanel {

	private MailServiceAsync mailxelService = null;
	private MailxelPanel mailxelPanel = null;
	private Image logo = null;
	private Label msgCountAct = new Label();

	public MailxelMainToolBar(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

		this.mailxelService = mailxelService;
		this.mailxelPanel = mailxelPanel;

		logo = new Image("img/mailxel.png");
		logo.setTitle("MailXel " + Version.getVersion());
		logo.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				PopupPanel pp = new PopupPanel(true);
				DisclosurePanel dp = new DisclosurePanel("MailXel " + Version.getVersion());
				dp.setWidth("400px");
				dp.setOpen(true);

				HTML html = new HTML();
				StringBuffer sb = new StringBuffer();
				sb.append("(c) 2008-2010 by Simon Hefti. All rights reserved.<br/>");
				sb.append("<p>mailxel is licensed under the <a href=\"http://www.eclipse.org/legal/epl-v10.html\">EPL 1.0</a>. mailxel is distributed on an \"AS IS\" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.");
				sb.append("<p>mailxel relies on the following components:");
				sb.append("<ul>");
				sb.append("<li>GWT, <a href=\"http://code.google.com/webtoolkit\">http://code.google.com/webtoolkit</a></li>");
				sb.append("<li>sqlite-jdbc, <a href=\"http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC\">http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC</a></li>");
				sb.append("<li>(and thus on sqlite itself, <a href=\"http://www.sqlite.org\">http://www.sqlite.org</a>)</li>");
				sb.append("<li>Java Mail API, <a href=\"http://java.sun.com/products/javamail\">http://java.sun.com/products/javamail</a></li>");
				sb.append("<li>jetty servlet container, <a href=\"http://www.eclipse.org/jetty/\">http://www.eclipse.org/jetty/</a></li>");
				sb.append("<li>fugue-icons, <a href=\"http://code.google.com/p/fugue-icons-src/\">http://code.google.com/p/fugue-icons-src/</a></li>");
				sb.append("<li>jsoup, <a href=\"http://jsoup.org\">http://jsoup.org</a></li>");
				sb.append("</ul>");
				html.setHTML(sb.toString());
				dp.add(html);
				dp.setOpen(true);

				pp.add(dp);
				pp.show();
			}
		});

		Image home = new Image("img/find.png");
		home.setTitle("Search");
		home.setStylePrimaryName("mailxel-toolbar-item");
		home.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				Panel panel = new MailOverviewGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(panel, "Search");
			}
		});

		final Image query = new Image("img/document-task.png");
		query.setTitle("Search (predefined query)");
		query.setStylePrimaryName("mailxel-toolbar-item");
		query.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				final StatusItem si = mailxelPanel.statusStart("retrieve stored message queries");

				mailxelService.searchQueries(MessageQueryTO.T_MESSAGE_QUERY, null,
						new AsyncCallback<List<MessageQueryTO>>() {

							public void onFailure(Throwable caught) {
								si.error(caught);
							}

							public void onSuccess(List<MessageQueryTO> result) {
								si.done();
								if (null != result && result.size() > 0) {
									PopupMenu popupMenu = new PopupMenu(query);
									for (MessageQueryTO mqTO : result) {
										String name = mqTO.shortname + " (" + UIUtil.shorten(mqTO.name) + ")";
										MenuItem menuItem = new MenuItem(name, new MessageQueryCommand(mailxelService,
												mailxelPanel, popupMenu, mqTO));
										String url = DirectMailServiceUtil.getIconURL(mqTO.iconId);
										if (null != url) {
											String html = "<img src=\"" + url + "\"/>&nbsp;" + name;
											menuItem.setHTML(html);
										}
										popupMenu.addItem(menuItem);
									}
									popupMenu.show();
								}
							}
						});
			}
		});

		Image mailnew = new Image("img/mail-new.png");
		mailnew.setTitle("New Mail");
		mailnew.setStylePrimaryName("mailxel-toolbar-item");
		mailnew.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				final MailSendGrid mailSendGrid = new MailSendGrid(mailxelService, mailxelPanel, null,
						MailSendGrid.TYPE_NEW);
				mailxelPanel.addTab(mailSendGrid, "New Mail");
			}
		});

		Image noteToSelf = new Image("img/note.png");
		noteToSelf.setTitle("Note to myself");
		noteToSelf.setStylePrimaryName("mailxel-toolbar-item");
		noteToSelf.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				final MailSendGrid mailSendGrid = new MailSendGrid(mailxelService, mailxelPanel, null,
						MailSendGrid.TYPE_SELF);
				mailxelPanel.addTab(mailSendGrid, "New Note");
			}
		});

		Image contacts = new Image("img/address-book.png");
		contacts.setTitle("Address Book");
		contacts.setStylePrimaryName("mailxel-toolbar-item");
		contacts.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				AddressOverviewGrid ag = new AddressOverviewGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(ag, "Contacts");
			}
		});

		/**
		 * mail download menu on click, a menu with the available accounts is
		 * displayed, allowing the user is asked to choose the data source.
		 */
		final Image download = new Image("img/download-mail.png");
		download.setTitle("Mail download");
		download.setStylePrimaryName("mailxel-toolbar-item");
		download.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				ConfigTO configTO = mailxelPanel.getConfig();
				String[] accounts = configTO.accountNames;

				if (null != accounts && accounts.length > 0) {

					PopupMenu popupMenu = new PopupMenu(download);
					// first item: allow download from all known accounts
					MenuItem menuItem = new MenuItem("Scan all accounts", new ScanMailFolderCommand(mailxelService,
							mailxelPanel, popupMenu, accounts));
					popupMenu.addItem(menuItem);

					// add one menu item per account
					for (int i = 0; i < accounts.length; i++) {
						String[] selectedAccount = new String[1];
						selectedAccount[0] = accounts[i];
						menuItem = new MenuItem(accounts[i], new ScanMailFolderCommand(mailxelService, mailxelPanel,
								popupMenu, selectedAccount));
						popupMenu.addItem(menuItem);
					}
					popupMenu.show();
				}
			}
		});

		final Image reorgMailFolder = new Image("img/wand.png");
		reorgMailFolder.setTitle("reorganize mail folder");
		reorgMailFolder.setStylePrimaryName("mailxel-toolbar-item");
		reorgMailFolder.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				ConfigTO configTO = mailxelPanel.getConfig();
				String[] accounts = configTO.accountNames;

				if (null != accounts && accounts.length > 0) {

					PopupMenu popupMenu = new PopupMenu(reorgMailFolder);
					// first item: allow reorg from all known accounts
					MenuItem menuItem = new MenuItem("All accounts", new ReorgMailFolderCommand(mailxelService,
							mailxelPanel, popupMenu, reorgMailFolder, accounts));
					popupMenu.addItem(menuItem);

					// add one menu item per account
					for (int i = 0; i < accounts.length; i++) {
						String[] selectedAccount = new String[1];
						selectedAccount[0] = accounts[i];
						menuItem = new MenuItem(accounts[i], new ReorgMailFolderCommand(mailxelService, mailxelPanel,
								popupMenu, reorgMailFolder, selectedAccount));
						popupMenu.addItem(menuItem);
					}
					popupMenu.show();
				}
			}
		});

		final Image categories = new Image("img/tags.png");
		categories.setTitle("Manage Categories");
		categories.setStylePrimaryName("mailxel-toolbar-item");
		categories.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {
				CategoryOverviewGrid cog = new CategoryOverviewGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(cog, "Categories");
			}
		});

		final Image setup = new Image("img/preferences-system.png");
		setup.setTitle("System Setup");
		setup.setStylePrimaryName("mailxel-toolbar-item");
		setup.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				// ConfigTabPanel cg = new ConfigTabPanel();
				ConfigGrid cg = new ConfigGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(cg, "Setup");
			}
		});

		final Image login = new Image("img/lock.png");
		login.setTitle("Login");
		login.setStylePrimaryName("mailxel-toolbar-item");
		login.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				// create login box
				LoginPanel loginPanel = new LoginPanel(mailxelService, mailxelPanel);
				int x = login.getAbsoluteLeft();
				int y = login.getAbsoluteTop();
				loginPanel.setPopupPosition(x, y);
				loginPanel.show();
			}
		});

		final Image additional = new Image("img/context-menu.png");
		additional.setTitle("Additional functions");
		additional.setStylePrimaryName("mailxel-toolbar-item");

		final PopupCommand importMboxCommand = new PopupCommand() {

			public void execute() {

				final PopupPanel pup = new PopupPanel(true);
				HorizontalPanel hp = new HorizontalPanel();
				final TextBox tb = new TextBox();
				tb.setWidth("300px");
				hp.add(tb);

				Button b = new Button();
				b.setText("import");
				b.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent sender) {
						String name = tb.getText();
						if (null != name) {
							name = name.trim();
							if (name.length() > 0) {
								final StatusItem si = mailxelPanel.statusStart("Import from mbox: " + name);
								mailxelService.importMboxFile(name, new AsyncCallback<Void>() {

									public void onFailure(Throwable caught) {
										si.error(caught);
									}

									public void onSuccess(Void result) {
										si.done();
									}
								});
								pup.hide();
							}
						}
					}
				});

				hp.add(b);
				pup.add(hp);

				int x = additional.getAbsoluteLeft();
				int y = additional.getAbsoluteTop();
				pup.setPopupPosition(x, y);
				/** show input box for path to mbox file */
				pup.show();
				/** hide the list of available additional commands */
				hide();
			}
		};

		final PopupCommand addressUploadCommand = new PopupCommand() {

			public void execute() {
				AddressUploadGrid ug = new AddressUploadGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(ug, "Address Upload");
				/** hide the list of available additional commands */
				hide();
			}
		};

		final Command showWelcomePanelCommand = new Command() {

			public void execute() {
				WelcomeToMailxelPanel wp = new WelcomeToMailxelPanel(mailxelService, mailxelPanel);
				mailxelPanel.addTab(wp, "Welcome");
			}
		};

		final PopupCommand deleteConfigCommand = new PopupCommand() {

			public void execute() {

				PopupPanel pop = new PopupPanel(true, true);
				HorizontalPanel hp = new HorizontalPanel();
				Label label = new Label("Really delete all configuration?");
				hp.add(label);
				Button b = new Button();
				b.setText("Ok");
				b.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						final StatusItem si = mailxelPanel.statusStart("deleting configuration");
						mailxelService.deleteConfig(new AsyncCallback<Void>() {

							public void onFailure(Throwable caught) {
								si.error(caught);
							}

							public void onSuccess(Void result) {
								si.done();
							}
						});
					}
				});
				hp.add(b);
				pop.add(hp);

				int x = additional.getAbsoluteLeft();
				int y = additional.getAbsoluteTop();
				pop.setPopupPosition(x, y);
				pop.show();
				/** hide the list of available additional commands */
				hide();
			}
		};

		final Command updateToMeFlagCommand = new Command() {

			public void execute() {
				final StatusItem si = mailxelPanel.statusStart("Update 'to me' flag");
				mailxelService.updateToMeFlag(new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						si.error(caught);
					}

					public void onSuccess(Void result) {
						si.done();
					}
				});
			}
		};

		final Command updateFromMeFlagCommand = new Command() {

			public void execute() {
				final StatusItem si = mailxelPanel.statusStart("Update 'from me' flag");
				mailxelService.updateFromMeFlag(new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						si.error(caught);
					}

					public void onSuccess(Void result) {
						si.done();
					}
				});
			}
		};

		final Command showStatisticsCommand = new Command() {

			public void execute() {
				StatisticsGrid sg = new StatisticsGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(sg, "Statistics");
			}
		};

		final Command showIconsCommand = new Command() {

			public void execute() {
				IconOverviewGrid og = new IconOverviewGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(og, "Icons");
			}
		};

		final Command showMessageQueriesCommand = new Command() {

			public void execute() {
				MessageQueryOverviewGrid mqog = new MessageQueryOverviewGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(mqog, "Message Queries");
			}
		};

		final Command showAttachmentGridCommand = new Command() {

			public void execute() {
				AttachmentOverviewGrid aog = new AttachmentOverviewGrid(mailxelService, mailxelPanel);
				mailxelPanel.addTab(aog, "Attachment Overview");
			}
		};

		final Command closeAllTabsCommand = new Command() {

			public void execute() {
				mailxelPanel.closeAllNonEditTabs();
			}
		};

		final Command dbHousekeeping = new Command() {

			public void execute() {
				final StatusItem si = mailxelPanel.statusStart("DB housekeeping");
				mailxelService.housekeeping(new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						si.error(caught);
					}

					public void onSuccess(String result) {
						if (result.startsWith("200 OK")) {
							si.done();
						} else {
							si.error(result);
						}
					}
				});
			}
		};

		final Command messageCount = new Command() {

			public void execute() {
				updateCounts();
			}
		};

		additional.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent sender) {

				MenuBar popupMenuBar = new MenuBar(true);
				PopupPanel popupPanel = new PopupPanel(true);

				MenuItem menuItem = new MenuItem("Attachment Overview", showAttachmentGridCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Close all Tabs", closeAllTabsCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Message Queries", showMessageQueriesCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("DB Housekeeping", dbHousekeeping);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("update pending messages count", messageCount);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Statistics", showStatisticsCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Icons", showIconsCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Import mbox file", importMboxCommand);
				importMboxCommand.setPopupPanel(popupPanel);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Address upload", addressUploadCommand);
				addressUploadCommand.setPopupPanel(popupPanel);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Welcome", showWelcomePanelCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Delete existing configuration", deleteConfigCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Update 'from me' flag", updateFromMeFlagCommand);
				popupMenuBar.addItem(menuItem);

				menuItem = new MenuItem("Update 'to me' flag", updateToMeFlagCommand);
				popupMenuBar.addItem(menuItem);

				popupMenuBar.setVisible(true);
				popupPanel.add(popupMenuBar);

				int x = additional.getAbsoluteLeft();
				int y = additional.getAbsoluteTop();

				popupPanel.setPopupPosition(x, y);
				popupPanel.show();
			}
		});

		updateCounts();

		add(home);
		add(query);
		add(mailnew);
		add(noteToSelf);
		add(contacts);
		add(categories);
		add(download);
		add(reorgMailFolder);
		add(setup);
		add(login);
		add(additional);
		add(logo);
		add(msgCountAct);
	}

	public void updateCounts() {

		final StatusItem si = mailxelPanel.statusStart("counting messages");
		mailxelService.getTotalNumMessages(new AsyncCallback<Long>() {

			public void onFailure(Throwable caught) {
				si.error(caught);
			}

			public void onSuccess(Long result) {
				si.done(1000);
				logo.setTitle("MailXel " + Version.getVersion() + " proudly managing " + Long.toString(result)
						+ " mails");
			}
		});

		final StatusItem si2 = mailxelPanel.statusStart("count messages which require action");
		MessageQueryTO mqTO = new MessageQueryTO();
		mqTO.id = 899;
		CountMessageCallback cntMsgCb = new CountMessageCallback(si2, msgCountAct);
		mailxelService.count(mqTO, cntMsgCb);

	}

	public void startDownloadTimer(int delay) {
		// schedule mail download
		if (delay > 0) {
			Timer timer = new Timer() {

				public void run() {
					ScanMailFolderCommand cmd = new ScanMailFolderCommand(mailxelService, mailxelPanel, null, null);
					cmd.execute();
				}
			};
			timer.scheduleRepeating(delay * 60 * 1000);
		}

	}
}
