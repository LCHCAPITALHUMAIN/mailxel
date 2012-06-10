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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/** a horizontal panel which breaks over several lines */
public class MultiLinePanel extends VerticalPanel {

  private int maxLength = 100;
  private int totalChars = 0;
  private HorizontalPanel curPanel = null;
  private String separator = null;
  // fixme: find a better way for widget removal
  private Map<Widget, HorizontalPanel> widgetPanelMap = new HashMap<Widget, HorizontalPanel>();

  /**
   * @param maxLength a new line is started after maxLength chars
   * @param separator widget used to separate entries
   * @param separatorLength length of separator widget, in chars
   */
  public MultiLinePanel(final int maxLength, final String separator) {
    this.maxLength = maxLength;
    this.separator = separator;
    curPanel = new HorizontalPanel();
    totalChars = 0;
    add(curPanel);
  }

  /**
   * @param maxLength a new line is started after maxLength chars
   */
  public MultiLinePanel(final int maxLength) {
    this.maxLength = maxLength;
    this.separator = null;
    curPanel = new HorizontalPanel();
    totalChars = 0;
    add(curPanel);
  }

  public void setSeparator(final String separator) {
    this.separator = separator;
  }

  /**
   * add new widget, create new line if necessary
   * 
   * @param widget widget to be added
   * @param length length, in chars, of this widget
   */
  public void addWidget(final Widget widget, final int length) {

    if (totalChars > maxLength) {
      // start a new line
      curPanel = new HorizontalPanel();
      add(curPanel);
      totalChars = 0;
    }
    if (null != separator && curPanel.getWidgetCount() > 0) {
      curPanel.add(new Label(separator));
      totalChars += separator.length();
    }
    curPanel.add(widget);
    widgetPanelMap.put(widget, curPanel);
    totalChars += length;
  }

  public void removeWidget(final Widget widget) {
    HorizontalPanel p = widgetPanelMap.get(widget);
    p.remove(widget);
    widgetPanelMap.remove(widget);
  }
}
