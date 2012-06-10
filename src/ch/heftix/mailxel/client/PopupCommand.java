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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;


public abstract class PopupCommand implements Command {

  protected PopupPanel popupPanel = null;

  public void setPopupPanel(final PopupPanel popupPanel) {
    this.popupPanel = popupPanel;
  }

  public void hide() {
    if (null != popupPanel) {
      popupPanel.hide();
    }
  }

}
