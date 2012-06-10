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

import ch.heftix.mailxel.client.to.ConfigTO;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;


public class LoginPanel extends PopupPanel {

  public LoginPanel(final MailServiceAsync mailxelService, final MailxelPanel mailxelPanel) {

    super(true);


    FlexTable grid = new FlexTable();
    // header
    grid.setText(0, 0, "Account");
    grid.setText(0, 1, "Password");
    // send account
    grid.setText(1, 0, "Send");
    final PasswordTextBox smtb = new PasswordTextBox();
    grid.setWidget(1, 1, smtb);
    LoginButton loginSMTPButton = new LoginButton(mailxelService, MailService.ACCOUNT_SEND, smtb);
    grid.setWidget(1, 2, loginSMTPButton);

    // one line per account
    ConfigTO configTO = mailxelPanel.getConfig();
    String[] accounts = configTO.accountNames;
    for (int i = 0; i < accounts.length; i++) {
      final String account = accounts[i];
      // label
      grid.setText(i + 2, 0, account);
      // field
      final PasswordTextBox tb = new PasswordTextBox();
      grid.setWidget(i + 2, 1, tb);
      // button
      final LoginButton button = new LoginButton(mailxelService, account, tb);
      tb.addKeyPressHandler(new KeyPressHandler() {

        public void onKeyPress(KeyPressEvent event) {
          if (event.getCharCode() == KeyCodes.KEY_ENTER) {
            button.doLogin();
          }
        }
      });
      grid.setWidget(i + 2, 2, button);
    }
    add(grid);
  }

}
