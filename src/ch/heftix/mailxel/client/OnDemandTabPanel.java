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

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * single child: no tabs displayed; more children: display first widget in first
 * tab and put second widget in additional tab
 */
public class OnDemandTabPanel extends VerticalPanel {

  private Widget single = null;
  private String singleTabText = null;
  private TabPanel tabPanel = null;

  public OnDemandTabPanel() {

  }

  public void add(final Widget w, final String tabText) {
    if (null == single) {
      single = w;
      singleTabText = tabText;
      super.add(w);
    } else {
      if (null == tabPanel) {
        // morph to tab Panel
        tabPanel = new TabPanel();
        remove(single);
        tabPanel.add(single, singleTabText);
        add(tabPanel);
      }
      tabPanel.add(w, tabText);
      tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
    }
  }

}
