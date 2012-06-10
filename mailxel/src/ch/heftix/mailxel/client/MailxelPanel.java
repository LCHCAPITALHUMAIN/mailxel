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
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import ch.heftix.mailxel.client.to.ConfigTO;

/**
 */
public class MailxelPanel extends DockLayoutPanel {

  public static final int MAX_DETAIL_TABS_OPEN = 10;

  // private TabPanel tabPanel = new TabPanel();
  private TabLayoutPanel tabPanel = new TabLayoutPanel(3, Unit.EM);
  private MailxelMainToolBar toolBar = null;
  private ConfigTO configTO = null;
  private StatusInfoBar statusBar = new StatusInfoBar();

  public MailxelPanel(final MailServiceAsync mailxelService) {

    super(Unit.EM);

    Window.addWindowClosingHandler(new Window.ClosingHandler() {

      public void onWindowClosing(ClosingEvent event) {
        // check for edit-in-progress
        if (hasEditTab()) {
          event.setMessage("mailxel: do you want to discard unsaved edits? All data will be lost.");
        }
      }
    });

    final StatusItem si = statusStart("reading configuration");

    mailxelService.getConfig(new AsyncCallback<ConfigTO>() {

      public void onFailure(Throwable caught) {
        si.error(caught);
        // start welcome wizard
        WelcomeToMailxelPanel wp = new WelcomeToMailxelPanel(mailxelService, MailxelPanel.this);
        addTab(wp, "Welcome");
      }

      public void onSuccess(ConfigTO result) {
        si.done(10000);
        configTO = result;
        if (null == configTO.accountNames || configTO.accountNames.length < 1) {
          WelcomeToMailxelPanel wp = new WelcomeToMailxelPanel(mailxelService, MailxelPanel.this);
          addTab(wp, "Welcome");
        }
      }

    });

    toolBar = new MailxelMainToolBar(mailxelService, this);
    addNorth(toolBar, 3);
    toolBar.add(statusBar);
    // addSouth(statusBar, 3);
    // RootLayoutPanel.get().add(tabLPanel);
    add(tabPanel);
    // add(tabLPanel);

    Panel p = new MailOverviewGrid(mailxelService, this);
    tabPanel.add(p, "Search");
    tabPanel.selectTab(0);

    if (null != configTO && configTO.downloadMailDelay > 0) {
      startDownloadTimer(configTO.downloadMailDelay);
    }

  }

  public void startDownloadTimer(int delay) {
    if (delay > 0) {
      toolBar.startDownloadTimer(delay);
    }
  }

  public void selectTab(final int idx) {
    tabPanel.selectTab(idx);
  }

  public void closeTab(final Widget widget) {
    tabPanel.remove(widget);
    int wc = tabPanel.getWidgetCount();
    if (wc > 0) {
      tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
    }
  }

  public void closeAllTabs() {
    int len = tabPanel.getWidgetCount();
    List<Widget> widgets = new ArrayList<Widget>();
    for (int i = 1; i < len; i++) {
      widgets.add(tabPanel.getWidget(i));
    }
    for (Widget widget : widgets) {
      int idx = tabPanel.getWidgetIndex(widget);
      tabPanel.remove(idx);
    }
    tabPanel.selectTab(0);
  }

  public void closeAllNonEditTabs() {
    int len = tabPanel.getWidgetCount();
    List<Widget> widgets = new ArrayList<Widget>();
    for (int i = 1; i < len; i++) {
      if (!isEditTab(i)) {
        widgets.add(tabPanel.getWidget(i));
      }
    }
    for (Widget widget : widgets) {
      int idx = tabPanel.getWidgetIndex(widget);
      tabPanel.remove(idx);
    }
    tabPanel.selectTab(0);
  }

  /**
   * add a tab to the panel
   * 
   * @param widget mail detail view
   * @param tabText tab text
   * @return tab (for later change of tab description)
   */
  public MailxelTab addTab(final MailDetailGrid widget, final String tabText) {

    MailxelTab tab = pAddTab(widget, tabText);
    widget.setMailxelTab(tab);

    return tab;
  }

  public MailxelTab addTab(final MessageQueryEditGrid widget, final String tabText) {

    MailxelTab tab = pAddTab(widget, tabText);
    widget.setMailxelTab(tab);

    return tab;
  }

  /**
   * add a tab to the panel
   * 
   * @return tab (for later change of tab description)
   */
  public MailxelTab addTab(final Widget widget, final String tabText) {

    MailxelTab tab = pAddTab(widget, tabText);

    return tab;
  }


  private MailxelTab pAddTab(final Widget widget, final String tabText) {

    MailxelTab tab = new MailxelTab(this, tabText, widget);

    tabPanel.add(widget, tab);

    // auto-close tabs if more than N tabs are open
    int cnt = tabPanel.getWidgetCount();
    if (cnt > MAX_DETAIL_TABS_OPEN) {
      int idx = getIndexOfFirstNonEditTab();
      if (idx != -1) {
        tabPanel.remove(idx);
      }
    }
    tabPanel.selectTab(tabPanel.getWidgetCount() - 1);

    return tab;
  }

  private int getIndexOfFirstNonEditTab() {
    int res = -1;
    int cnt = tabPanel.getWidgetCount();
    for (int i = 1; i < cnt; i++) {
      Widget w = tabPanel.getWidget(i);
      if (w instanceof MailSendGrid) {
        // keep edit tabs open
      } else {
        res = i;
        break;
      }
    }
    return res;
  }

  /**
   * @return true if tab at given index is an edit
   */
  private boolean isEditTab(final int idx) {
    boolean res = false;
    Widget w = tabPanel.getWidget(idx);
    if (w instanceof MailSendGrid) {
      res = true;
    }
    return res;
  }

  private int countNonEditTabs() {
    int res = 0;
    int cnt = tabPanel.getWidgetCount();
    for (int i = 1; i < cnt; i++) {
      if (!isEditTab(i)) {
        res++;
      }
    }
    return res;
  }

  private int countEditTabs() {
    int res = 0;
    int cnt = tabPanel.getWidgetCount();
    for (int i = 1; i < cnt; i++) {
      if (isEditTab(i)) {
        res++;
      }
    }
    return res;
  }

  private boolean hasEditTab() {
    boolean res = false;
    int cnt = tabPanel.getWidgetCount();
    for (int i = 1; i < cnt; i++) {
      if (isEditTab(i)) {
        res = true;
        break;

      }
    }
    return res;
  }

  public void statusError(final String msg, final Throwable caught) {
    statusBar.info(msg + ":" + caught);
  }

  public void statusStart(final ServiceCall sc) {
    statusBar.start(sc);
  }


  public StatusItem statusStart(final String msg) {
    return statusBar.start(msg);
  }

  public ConfigTO getConfig() {
    return configTO;
  }

  public void setConfig(final ConfigTO configTO) {
    this.configTO = configTO;
  }

  public void updateMailCounts() {

  }

}
