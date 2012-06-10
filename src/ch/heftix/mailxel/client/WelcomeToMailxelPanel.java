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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import ch.heftix.mailxel.client.to.AccountConfigTO;
import ch.heftix.mailxel.client.to.ConfigTO;

/**
 */
public class WelcomeToMailxelPanel extends VerticalPanel {

  public static final int IDX_mail = 0;
  public static final int IDX_data = 1;
  public static final int IDX_smtp = 2;
  public static final int IDX_imap = 3;
  public static final int IDX_COLS = 4;

  private TextBox[] boxes = new TextBox[IDX_COLS];

  public WelcomeToMailxelPanel(final MailServiceAsync mailxelService,
      final MailxelPanel mailxelPanel) {

    StringBuffer sb = new StringBuffer(512);
    sb.append("<div style=\"welcome\"><p><b>Welcome and Thank You</b> for choosing mailxel");
    sb.append("<p>This seems to be the first time you start mailxel (*).");
    sb.append("<p>For a quick start, use the configuration form below.");
    sb.append("<p>Note: you can always edit your settings using the preferences");
    sb.append("<img src=\"img/preferences-system.png\"/> function of the menu");

    HTML html = new HTML(sb.toString());
    add(html);

    FlexTable grid = new FlexTable();
    int idx = IDX_mail;
    grid.setText(idx, 0, "your main e-mail address");
    boxes[idx] = new TextBox();
    boxes[idx].setWidth("200px");
    boxes[idx].setText("example: some.body@somewhere.com");
    grid.setWidget(idx, 1, boxes[idx]);

    idx = IDX_data;
    grid.setText(idx, 0, "mailxel data directory");
    boxes[idx] = new TextBox();
    boxes[idx].setWidth("200px");
    boxes[idx].setText("example: d:/data/mailxel");
    grid.setWidget(idx, 1, boxes[idx]);

    idx = IDX_smtp;
    grid.setText(idx, 0, "your SMTP host");
    boxes[idx] = new TextBox();
    boxes[idx].setWidth("200px");
    boxes[idx].setText("example: smtp.somewhere.com");
    grid.setWidget(idx, 1, boxes[idx]);

    idx = IDX_imap;
    grid.setText(idx, 0, "your IMAP host");
    boxes[idx] = new TextBox();
    boxes[idx].setWidth("200px");
    boxes[idx].setText("example: imap.somewhere.com");
    grid.setWidget(idx, 1, boxes[idx]);

    add(grid);

    Button b = new Button();
    add(b);
    b.setText("Store and Start");
    b.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {

        AccountConfigTO aTO = new AccountConfigTO();
        String[] sf = {"inbox", "sent"};
        String[] ef = {"trash"};

        aTO.name = "main";
        aTO.protocol = "imap";
        aTO.isSSL = false;
        aTO.port = "143";
        aTO.user = boxes[IDX_mail].getText();
        aTO.server = boxes[IDX_imap].getText();
        aTO.scannedfolders = sf;
        aTO.excludedfolders = ef;
        aTO.reuseSendPassword = false;
        aTO.reorgRules = new String[0];

        String[] accountNames = {"main"};
        AccountConfigTO[] aTOs = new AccountConfigTO[1];
        aTOs[0] = aTO;

        final ConfigTO cTO = new ConfigTO();

        cTO.localstore = boxes[IDX_data].getText();
        cTO.smtpHost = boxes[IDX_smtp].getText();
        cTO.smtpPort = "25";
        cTO.smtpUser = "";
        cTO.smtpPassword = "";

        cTO.sentHandlingBCC = false;
        cTO.sentHandlingStoreLocally = true;
        cTO.sentHandlingCopySent = true;
        cTO.sentHandlingCopyAccountName = "main";
        cTO.sentHandlingCopyFolderName = "sent";

        cTO.logConfiguration = LOGConstants.PRODUCTION;
        cTO.maxSearchRows = 30;
        cTO.maxAddressSuggestions = 10;

        cTO.me = boxes[IDX_mail].getText();

        cTO.accountNames = accountNames;
        cTO.accounts = aTOs;

        final StatusItem si = mailxelPanel.statusStart("saving welcome configuration");
        mailxelService.saveConfig(cTO, new AsyncCallback<Void>() {

          public void onSuccess(Void result) {
            si.done();
            mailxelPanel.setConfig(cTO);
            ConfigGrid cg = new ConfigGrid(mailxelService, mailxelPanel);
            mailxelPanel.addTab(cg, "Settings");
            mailxelPanel.closeTab(WelcomeToMailxelPanel.this);
          }

          public void onFailure(Throwable caught) {
            si.error(caught);
          }
        });
      }
    });

    sb = new StringBuffer(512);
    sb.append("<p style=\"footnote\">(*) If this is not the case: please ");
    sb.append("<a href=\"http://code.google.com/p/mailxel/issues/entry\">");
    sb.append("report an error</a>.");

    html = new HTML(sb.toString());
    add(html);

  }
}
