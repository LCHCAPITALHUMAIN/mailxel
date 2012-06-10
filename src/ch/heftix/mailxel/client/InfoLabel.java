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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class InfoLabel extends HorizontalPanel {

  private Image img = new Image("img/help.png");
  private Label lbl = new Label();

  public InfoLabel() {
    add(img);
    add(lbl);
  }

  public InfoLabel(final String helpTopic,
      final String text,
      final String title,
      final MailxelPanel mailxelPanel) {
    this();
    setText(text);
    setTitle(title);
    img.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        MailxelHelpPanel mhp = new MailxelHelpPanel(helpTopic, mailxelPanel);

        int x = img.getAbsoluteLeft();
        int y = img.getAbsoluteTop();

        mhp.setPopupPosition(x, y);
        mhp.show();
      }
    });
  }

  public void setText(final String text) {
    lbl.setText(text);
  }

  public void setTitle(final String text) {
    img.setTitle(text);
  }
}
