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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;


public class StatusItem extends HorizontalPanel {

  private Image working = new Image("img/process-working.gif");
  private Label lTitle = new Label();
  private Label lDone = new Label();
  public boolean isDone = false;
  private StatusInfoBar statusInfoBar = null;
  public String title;

  public StatusItem(final String title, final StatusInfoBar statusInfoBar) {
    
    this.title = title;
    
    setSpacing(1);

    this.statusInfoBar = statusInfoBar;

    add(working);
    add(lTitle);
    add(lDone);

    String tmp = title;
    if (null == title) {
      tmp = "";
    }
    if (!tmp.endsWith(":")) {
      tmp = tmp + ":";
    }
    lTitle.setText(tmp);

    working.setVisible(false);
    isDone = false;
  }

  public void setInfo(String info) {
    working.setUrl("img/infocard-small.png");
    lDone.setText(info);
    working.setVisible(true);
  }

  public void start() {
    working.setVisible(true);
    lDone.setText("started");
  }

  public void done(final String message, final int autoHideAfterMs) {
    working.setUrl("img/tick.png");
    // working.setVisible(false);
    lDone.setText(message);
    isDone = true;
    if (autoHideAfterMs > 0) {
      Timer timer = new Timer() {

        public void run() {
          statusInfoBar.removeDones();
        }
      };
      timer.schedule(autoHideAfterMs);
    }
  }

  public void done(final int autoHideAfterMs) {
    done("done", autoHideAfterMs);
  }

  public void done() {
    done("done", 0);
  }

  public void error(final String message) {
    working.setUrl("img/exclamation-white.png");
    lDone.setText(message);
    isDone = true;
  }

  public void error(final Throwable caught) {
    working.setUrl("img/exclamation-white.png");
    lDone.setText(caught.getMessage());
    working.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        HTML html = new HTML(UIUtil.stackTrace(caught));
        PopupPanel pop = new PopupPanel(true);
        pop.add(html);
        int x = working.getAbsoluteLeft();
        int y = working.getAbsoluteTop();
        pop.setPopupPosition(x, y);
        pop.show();
      }
    });
    isDone = true;
  }

}
