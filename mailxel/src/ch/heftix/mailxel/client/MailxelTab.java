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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 */
public class MailxelTab extends HorizontalPanel {

  private Label desc = new Label();

  public MailxelTab(final MailxelPanel mailxelPanel, final String text, final Widget widget) {

    setTabText(text);
    add(desc);
    final Image img = new Image("img/close.png");
    img.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        boolean closeOk = true;
        if (widget instanceof MailSendGrid) {
          // show modal dialog
          closeOk = Window.confirm("discard unsaved edits?");
        }
        if (closeOk) {
          mailxelPanel.closeTab(widget);
        }
      }
    });
    add(img);
  }

  public void setTabText(final String text) {

    String tabDesc = UIUtil.shorten(text, 16, "..");

    desc.setText(tabDesc);
  }
}
