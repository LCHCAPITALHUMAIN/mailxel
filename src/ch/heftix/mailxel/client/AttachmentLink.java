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
import com.google.gwt.user.client.ui.Anchor;

public class AttachmentLink extends Anchor {

  public AttachmentLink(final String text, final String target) {

    setText(text);
    addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        Window.open(target, "_blank",
            "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=no");
      }
    });

  }


}
