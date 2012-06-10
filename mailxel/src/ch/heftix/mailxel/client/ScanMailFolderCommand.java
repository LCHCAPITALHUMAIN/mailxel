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

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * initiate mail download (using {@link MailService}) based on user request
 */
public class ScanMailFolderCommand extends PopupCommand {

  private String[] accounts = null;
  protected MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;

  public ScanMailFolderCommand(final MailServiceAsync mailxelService,
      final MailxelPanel mailxelPanel,
      final PopupPanel popupPanel,
      final String[] accounts) {

    super();
    setPopupPanel(popupPanel);
    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;
    this.accounts = accounts;
  }

  public void execute() {

    final StatusItem si = mailxelPanel.statusStart("mail download");

    // hide menu
    if( null != popupPanel) {
      popupPanel.hide();
    }

    mailxelService.scanMailFolder(accounts, new AsyncCallback<String>() {

      public void onFailure(Throwable caught) {
        si.error(caught);
      }

      public void onSuccess(String result) {
        if (result.startsWith("401 NOK")) {
          // not logged in
          si.error("please log in first");

          LoginPanel loginPanel = new LoginPanel(mailxelService, mailxelPanel);
          loginPanel.setPopupPosition(50, 50);

          loginPanel.show();

        } else if (!result.startsWith("200 OK")) {
          si.error(result);
        } else {
          si.done("completed on " + new Date(), 0);
        }
      }
    });
  }
}
