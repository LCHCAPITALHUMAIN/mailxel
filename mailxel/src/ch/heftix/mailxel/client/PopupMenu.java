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

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


public class PopupMenu extends PopupPanel {

  private MenuBar menuBar = new MenuBar(true);

  public PopupMenu(final Widget sourceWidget) {

    // create pop-up menu
    super(true);
    menuBar.setVisible(true);
    add(menuBar);

    int x = sourceWidget.getAbsoluteLeft();
    int y = sourceWidget.getAbsoluteTop();

    setPopupPosition(x, y);
  }

  public void addItem(final MenuItem menuItem) {

    menuBar.addItem(menuItem);
  }
}
