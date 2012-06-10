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
import com.google.gwt.user.client.ui.CheckBox;

public class CheckBoxWChangeDetection extends CheckBox {

  private boolean isDirty = false;

  public CheckBoxWChangeDetection() {
    super();
    addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent sender) {
        isDirty = true;
      }
    });
  }

  public void setDirty() {
    isDirty = true;
  }

  public boolean isDirty() {
    return isDirty;
  }
}
