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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class PopupWindow extends PopupPanel {

  private VerticalPanel vp = new VerticalPanel();
  private Image dock = new Image("img/dock.png");
  private Image close = new Image("img/cross.png");
  private HorizontalPanel titlePanel = new HorizontalPanel();
  private Label label = new Label();

  public PopupWindow(final String title,
      final Widget payload,
      final int width,
      final MailxelPanel mailxelPanel) {

    close.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        hide();
      }
    });
    close.setTitle("Close");
    dock.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        mailxelPanel.addTab(payload, title);
        PopupWindow.this.hide();
      }
    });
    dock.setTitle("Dock into tab bar");
    label.setText(title);
    titlePanel.add(label);
    titlePanel.add(dock);
    titlePanel.add(close);
    setWidth(width);
    vp.add(titlePanel);
    vp.add(payload);
    add(vp);
  }

  public PopupWindow(final String title, final Widget payload, final MailxelPanel mailxelPanel) {
    this(title, payload, 300, mailxelPanel);
  }

  public void setWidth(int width) {
    label.setWidth(Integer.toString(width) + "px");
    titlePanel.setWidth(Integer.toString(width + 32) + "px");
  }


}
