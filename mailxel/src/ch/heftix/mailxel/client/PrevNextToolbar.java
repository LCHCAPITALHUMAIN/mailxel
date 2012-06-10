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

import ch.heftix.mailxel.client.to.Envelope;

public class PrevNextToolbar extends HorizontalPanel {

  public PrevNextToolbar(final HasPrevNext prevNextProvider,
      final MailDetailDisplay mailDetailDisplay) {

    Image fwd = new Image("img/resultset_next.png");
    fwd.setTitle("Next");
    fwd.setStylePrimaryName("mailxel-toolbar-item");
    fwd.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        Envelope env = prevNextProvider.next();
        mailDetailDisplay.displayMailDetail(env.id);
      }
    });

    Image prev = new Image("img/resultset_previous.png");
    prev.setTitle("Next");
    prev.setStylePrimaryName("mailxel-toolbar-item");
    prev.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {

        Envelope env = prevNextProvider.prev();
        mailDetailDisplay.displayMailDetail(env.id);
      }
    });

    add(prev);
    add(fwd);
  }

}
