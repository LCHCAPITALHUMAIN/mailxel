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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;


public class CountMessageCallback implements AsyncCallback<Long> {

  StatusItem si2 = null;
  Label label = null;

  public CountMessageCallback(StatusItem si, Label label) {
    this.si2 = si;
    this.label = label;
  }

  public void onFailure(Throwable caught) {
    si2.error(caught);
  }

  public void onSuccess(Long result) {
    si2.done();
    label.setText(Long.toString(result));
    if (result > 50) {
      label.setStyleName("bg-red");
      // msgCountAct.setStylePrimaryName("bg-red");
    } else if (result > 20) {
      label.setStyleName("bg-orange");
    } else if (result > 10) {
      label.setStyleName("bg-yellow");
    } else if (result > 5) {
      label.setStyleName("bg-green");
    }
  }
}
