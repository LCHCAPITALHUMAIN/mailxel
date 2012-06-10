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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.HorizontalPanel;


public class StatusInfoBar extends HorizontalPanel {

  private List<StatusItem> statusItems = new ArrayList<StatusItem>();
  private Set<String> calls = new HashSet<String>();

  public StatusItem start(final ServiceCall sc) {
    String msg = sc.getMessage();
    removeDones();
    if (sc.runSynchronized()) {
      // check for running calls
      if (calls.contains(msg)) {
        info(msg, "already running");
        return null;
      }
    }
    StatusItem si = createStatusItem(msg);
    si.start();
    sc.run(si);
    calls.add(msg);
    return si;
  }

  public StatusItem start(final String title) {
    removeDones();
    StatusItem si = createStatusItem(title);
    si.start();
    return si;
  }

  public StatusItem info(final String msg, final String info) {
    removeDones();
    StatusItem si = createStatusItem(msg);
    si.setInfo(info);
    return si;
  }

  public StatusItem info(final String msg) {
    return info(msg, "");
  }

  public StatusItem createStatusItem(final String title) {

    StatusItem si = new StatusItem(title, this);
    statusItems.add(si);
    add(si);
    return si;
  }

  protected void removeDones() {
    List<StatusItem> items2 = new ArrayList<StatusItem>();
    for (StatusItem si : statusItems) {
      if (null != si && si.isDone) {
        remove(si);
        items2.add(si);
      }
    }
    for (StatusItem statusItem : items2) {
      statusItems.remove(statusItem);
      calls.remove(statusItem.title);
    }
  }
}
