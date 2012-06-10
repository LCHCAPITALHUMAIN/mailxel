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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class TextBoxWChangeDetection extends TextBox implements ValueChangeHandler<String> {

  private boolean isDirty = false;

  public TextBoxWChangeDetection() {
    super();
    addValueChangeHandler(this);
  }

  public void setDirty() {
    isDirty = true;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public void onValueChange(ValueChangeEvent<String> event) {
    setDirty();
  }
  
  public String getTextTrimmed() {
    String tmp = getText();
    if( null != tmp && tmp.length() > 0) {
     tmp = tmp.trim(); 
    }
    return tmp;
  }
}
