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

import java.util.List;

import ch.heftix.mailxel.client.to.Envelope;

public class MailSelectCallback implements MailxelAsyncCallback<List<Envelope>> {

  private StatusItem statusItem = null;
  private MailOverviewGrid mailOverviewGrid = null;

  public MailSelectCallback(final StatusItem statusItem, final MailOverviewGrid mailOverviewGrid) {
    this.mailOverviewGrid = mailOverviewGrid;
    setStatusItem(statusItem);
  }

  public void setStatusItem(StatusItem statusItem) {
    this.statusItem = statusItem;
  }

  public void onSuccess(List<Envelope> result) {

    statusItem.done("found " + Integer.toString(result.size()) + " messages.",0);
    mailOverviewGrid.fillGrid(result);
  }

  public void onFailure(Throwable caught) {
    statusItem.error(caught);
  }
}
