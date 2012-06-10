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
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;


public class LoginButton extends Image {

  public static final int LOGGED_IN = 1;
  public static final int LOGGED_OUT = 2;
  public static final int ERROR = 3;
  private int state = LOGGED_OUT;
  private HasText field = null;
  private String accountName = null;
  private MailServiceAsync mailxelService = null;

  public LoginButton(final MailServiceAsync mailxelService,
      final String accountName,
      final HasText field) {

    super("img/process-working.gif");
    this.field = field;
    this.accountName = accountName;
    this.mailxelService = mailxelService;

    addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        switch (state) {
          case LOGGED_IN:
            // log out
            doLogout();
            break;

          case LOGGED_OUT:
            // log in
            doLogin();
            break;

          default:
            // ERROR, re-try login
            doLogin();
            break;
        }
      }
    });

    mailxelService.isPasswordSet(accountName, new AsyncCallback<Boolean>() {

      public void onFailure(Throwable caught) {
        setUrl("img/exclamation");
        setTitle("connection error:" + caught);
        state = ERROR;
      }

      public void onSuccess(Boolean result) {
        if (result) {
          setUrl("img/tick.png");
          setTitle("Logout");
          state = LOGGED_IN;
        } else {
          setUrl("img/lock.png");
          setTitle("Login");
          state = LOGGED_OUT;
        }
      }
    });
  }

  void doLogin() {

    String val = field.getText();
    if (null != val && val.length() > 1) {
      mailxelService.setPassword(accountName, val, new AsyncCallback<Void>() {

        public void onFailure(Throwable caught) {
          setUrl("img/exclamation");
          setTitle("connection error:" + caught);
          state = ERROR;
        }

        public void onSuccess(Void result) {
          setUrl("img/tick.png");
          setTitle("Logout");
          state = LOGGED_IN;
        }
      });
    }
  }

  private void doLogout() {

    mailxelService.forgetPassword(accountName, new AsyncCallback<Void>() {

      public void onFailure(Throwable caught) {
        setUrl("img/exclamation");
        setTitle("connection error:" + caught);
        state = ERROR;
      }

      public void onSuccess(Void result) {
        setUrl("img/lock.png");
        setTitle("Login");
        state = LOGGED_OUT;
      }
    });
  }
}
