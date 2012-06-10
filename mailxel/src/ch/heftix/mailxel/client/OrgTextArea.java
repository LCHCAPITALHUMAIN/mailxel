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

import com.google.gwt.core.client.Duration;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class OrgTextArea extends TextArea {

  OrgModeSimple om = new OrgModeSimple();

  public OrgTextArea() {

    KeyboardListener kl = new KeyboardListener() {

      Duration sinceEscapePressed = null;

      public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        if (keyCode == KeyboardListener.KEY_ESCAPE) {
          sinceEscapePressed = new Duration();
        }
      }

      public void onKeyUp(Widget sender, char keyCode, int modifiers) {
      }

      public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        if ('q' == keyCode) {
          // System.out.println(sinceEscapePressed.elapsedMillis());
          if (null != sinceEscapePressed && (sinceEscapePressed.elapsedMillis() < 300)) {
            // format region
            TextArea ta = (TextArea) sender;
            String text = ta.getText();
            int pos = ta.getCursorPos();
            int len = ta.getSelectionLength();
            String replacement = om.prefixSelection(text, "> ", pos, len);
            ta.setText(replacement);
            cancelKey();
          }
        }
      }

    };

    addKeyboardListener(kl);

  }

}
