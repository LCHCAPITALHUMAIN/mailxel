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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * initiate mail folder reorganisation (using {@link MailService}) based on user
 * request
 */
public class ReorgMailFolderCommand extends PopupCommand {

  private String[] accounts = null;
  protected MailServiceAsync mailxelService = null;
  private MailxelPanel mailxelPanel = null;
  protected Image image = null;


  public ReorgMailFolderCommand(final MailServiceAsync mailxelService,
      final MailxelPanel mailxelPanel,
      final PopupPanel popupPanel,
      final Image image,
      final String[] accounts) {

    super();
    setPopupPanel(popupPanel);
    this.mailxelService = mailxelService;
    this.mailxelPanel = mailxelPanel;
    this.image = image;
    this.accounts = accounts;
  }

  public void execute() {

    final StatusItem si = mailxelPanel.statusStart("mail reorganization");

    // hide menu
    popupPanel.hide();

    mailxelService.reorgMailFolder(accounts, new AsyncCallback<String>() {

      public void onFailure(Throwable caught) {
        si.error(caught);
      }

      public void onSuccess(String result) {
        if (!result.startsWith("200 OK")) {
          si.error(result);
        } else {
          si.done("folder reorganization completed on " + new Date(), 0);
        }
      }
    });
  }
}
